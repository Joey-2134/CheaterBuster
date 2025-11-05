import os

import pandas as pd
from dotenv import load_dotenv
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

def evaluate_model(clf, X_test, y_test, show_plot=True):
    from sklearn.metrics import (confusion_matrix, roc_auc_score, precision_score, recall_score, f1_score)
    import matplotlib.pyplot as plt
    import seaborn as sns

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
