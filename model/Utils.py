import os
import time

import numpy as np
import pandas as pd
from dotenv import load_dotenv
from imblearn.combine import SMOTEENN
from sklearn.impute import KNNImputer
from sqlalchemy import create_engine
from sqlalchemy.engine import Engine

COLOUR_BANNED = '#e74c3c'        # Red
COLOUR_NON_BANNED = '#2ecc71'    # Green
COLOUR_BLUE = '#3498db'          # Blue

false, true = b'\x00', b'\x01'

def setup_database_connection(env_path: str = '../local/.env') -> Engine:
    load_dotenv(env_path)

    db_url = os.getenv('DB_URL')
    db_username = os.getenv('DB_USERNAME')
    db_password = os.getenv('DB_PASSWORD')
    db_name = os.getenv('DB_NAME', 'cheaterbuster')
    db_port = os.getenv('DB_PORT', '3306')

    if not all([db_url, db_username, db_password]):
        raise ValueError(
            "Missing database credentials! "
            "Please set DB_URL, DB_USERNAME, and DB_PASSWORD in local/.env"
        )

    connection_string = f'mysql+pymysql://{db_username}:{db_password}@{db_url}:{db_port}/{db_name}'

    print(f"Connecting to database...")
    engine = create_engine(connection_string)
    print("Connection successful!")

    return engine

def load_all_players(engine: Engine) -> pd.DataFrame:
    df = pd.read_sql("SELECT * FROM player_data", engine)
    print(f"Loaded {len(df)} players")
    return df

def load_data(csv_path='data.csv'):
    if os.path.exists(csv_path):
        print(f"Loading data from {csv_path}...")
        df = pd.read_csv(csv_path)

        # Convert has_ban from string representation back to bytes
        if df['has_ban'].dtype == 'object' and isinstance(df['has_ban'].iloc[0], str):
            df['has_ban'] = df['has_ban'].apply(lambda x: false if x == "b'\\x00'" else true)

        print(f"Loaded {len(df)} players from CSV")
        return df
    else:
        print(f"Loading data from MySQL server...")
        engine = setup_database_connection()
        df = load_all_players(engine)
        df.to_csv(csv_path, index=False)
        return df

def evaluate_model(clf, X_test, y_test, show_plot=True):
    from sklearn.metrics import (confusion_matrix, roc_auc_score, precision_score, recall_score, f1_score)
    import matplotlib.pyplot as plt
    import seaborn as sns

    print("Evaluating model...")

    y_pred = clf.predict(X_test)
    y_pred_proba = clf.predict_proba(X_test)[:, 1]

    cm = confusion_matrix(y_test, y_pred)
    if show_plot:
        fig, ax = plt.subplots(figsize=(10, 8))
        sns.heatmap(cm,
                    annot=True,
                    fmt='d',
                    cmap='Blues',
                    xticklabels=['Legit', 'Banned'],
                    yticklabels=['Legit', 'Banned'],
                    cbar_kws={'label': 'Count'},
                    ax=ax)
        ax.set_xlabel('Predicted Label', fontsize=14, fontweight='bold')
        ax.set_ylabel('True Label', fontsize=14, fontweight='bold')
        ax.set_title('Confusion Matrix - Test Set', fontsize=16, fontweight='bold')

        for i in range(2):
            for j in range(2):
                percentage = cm[i, j] / cm[i].sum() * 100
                ax.text(j + 0.5, i + 0.7, f'({percentage:.1f}%)',
                       ha='center', va='center', fontsize=11, color='gray')

        plt.tight_layout()
        plt.show()

    tn, fp, fn, tp = cm.ravel()
    fpr = fp / (fp + tn)
    fnr = fn / (fn + tp)
    precision = precision_score(y_test, y_pred)
    recall = recall_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    roc_auc = roc_auc_score(y_test, y_pred_proba)

    print("Metrics:")
    print(f"  ROC-AUC Score:           {roc_auc:.4f}")
    print(f"  Precision (Banned):      {precision:.4f}")
    print(f"  Recall (Banned):         {recall:.4f}")
    print(f"  F1-Score (Banned):       {f1:.4f}")

    print(f"  False Positive Rate:     {fpr:.4f} ({fp:,} legit players incorrectly flagged)")
    print(f"  False Negative Rate:     {fnr:.4f} ({fn:,} cheaters missed)")
    print(f"  True Positives:          {tp:,} cheaters correctly identified")
    print(f"  True Negatives:          {tn:,} legit players correctly identified")

def impute_data(player_data):
    start_time = time.time()
    print("Imputing data...")

    banned_player_data = player_data[player_data['has_ban'] == true]
    non_banned_player_data = player_data[player_data['has_ban'] == false]
    features_to_exclude = ['steam_id', 'created_at', 'updated_at', 'total_matches', 'rank_premier', 'rank_faceit_elo']
    numeric_cols = player_data.select_dtypes(include=['float64', 'int64']).columns.tolist()
    numeric_cols = [col for col in numeric_cols if col not in features_to_exclude]

    knn_imputer = KNNImputer(n_neighbors=5, weights='distance')
    banned_data = banned_player_data.copy()
    non_banned_data = non_banned_player_data.copy()

    for col in numeric_cols:
        banned_data.loc[banned_data[col] == 0, col] = np.nan
        non_banned_data.loc[non_banned_data[col] == 0, col] = np.nan

    banned_imputed_values = knn_imputer.fit_transform(banned_data[numeric_cols])
    banned_imputed = banned_data.copy()
    banned_imputed[numeric_cols] = banned_imputed_values

    non_banned_imputed_values = knn_imputer.fit_transform(non_banned_data[numeric_cols])
    non_banned_imputed = non_banned_data.copy()
    non_banned_imputed[numeric_cols] = non_banned_imputed_values

    elapsed_time = time.time() - start_time
    print(f"Data imputation completed in {elapsed_time} seconds\n")

    return pd.concat([banned_imputed, non_banned_imputed], ignore_index=True)

def resample_data(X_train, y_train):
    start_time = time.time()
    print("Resampling data...")

    smote_enn = SMOTEENN(random_state=42)
    X_train_resampled, y_train_resampled = smote_enn.fit_resample(X_train, y_train)
    print(f"\nOriginal distribution:")
    print(f"  Banned: {(y_train == 1).sum():,} ({(y_train == 1).sum()/len(y_train)*100:.2f}%)")
    print(f"  Non-banned: {(y_train == 0).sum():,} ({(y_train == 0).sum()/len(y_train)*100:.2f}%)")
    print(f"\nResampled distribution:")
    print(f"  Banned: {(y_train_resampled == 1).sum():,} ({(y_train_resampled == 1).sum()/len(y_train_resampled)*100:.2f}%)")
    print(f"  Non-banned: {(y_train_resampled == 0).sum():,} ({(y_train_resampled == 0).sum()/len(y_train_resampled)*100:.2f}%)")
    elapsed_time = time.time() - start_time
    print(f"\nData resampling completed in {elapsed_time} seconds\n")

    return X_train_resampled, y_train_resampled

