from flask import Flask, request, jsonify
import joblib
import numpy as np
import json
import logging
import os

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

MODEL_DIR = os.path.join(os.path.dirname(__file__), 'models')
model = None
imputer = None
feature_names = []

def load_artifacts():
    global model, imputer, feature_names

    try:
        # Load model
        model_path = os.path.join(MODEL_DIR, 'xgboost_model.pkl')
        logger.info(f"Loading model from {model_path}")
        model = joblib.load(model_path)
        logger.info("Model loaded successfully")

        # Load imputer
        imputer_path = os.path.join(MODEL_DIR, 'knn_imputer.pkl')
        if os.path.exists(imputer_path):
            logger.info(f"Loading imputer from {imputer_path}")
            imputer = joblib.load(imputer_path)
            logger.info("Imputer loaded successfully")

        config_path = os.path.join(MODEL_DIR, 'model_config.json')
        with open(config_path, 'r') as f:
            config = json.load(f)
            feature_names = config['features']
        logger.info(f"Loaded {len(feature_names)} features")

    except Exception as e:
        logger.error(f"Error loading model artifacts: {e}")
        raise

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint for ECS/ALB"""
    return jsonify({
        'status': 'healthy',
        'model_loaded': model is not None,
        'imputer_loaded': imputer is not None,
        'num_features': len(feature_names)
    }), 200

@app.route('/predict', methods=['POST'])
def predict():
    """
    Predict cheat probability for a single player

    Expected JSON body:
    {
        "accuracy_enemy_spotted": 32.5,
        "accuracy_head": 22.3,
        "counter_strafing_good_shots_ratio": 77.2,
        ... (27 features total)
    }

    Response:
    {
        "prediction": 1,           # 0=legit, 1=cheater
        "probability": 0.95,       # Probability of being a cheater (0-1)
        "confidence": 0.95,        # Confidence in prediction
        "risk_level": "HIGH"       # HIGH/MEDIUM/LOW/MINIMAL
    }
    """
    try:
        data = request.get_json()

        if not data:
            return jsonify({'error': 'No data provided'}), 400

        # Extract features in correct order
        features = []
        missing_features = []

        for feature_name in feature_names:
            value = data.get(feature_name)
            if value is None:
                missing_features.append(feature_name)
                value = 0  # Default to 0 for missing values
            features.append(float(value))

        if missing_features:
            logger.warning(f"Missing features (using 0): {missing_features}")

        # Convert to numpy array
        X = np.array([features])

        # Apply imputation if imputer is available
        if imputer is not None:
            # Convert zeros to NaN for imputation (matching training preprocessing)
            X_with_nan = X.copy()
            X_with_nan[X_with_nan == 0] = np.nan
            X = imputer.transform(X_with_nan)

        # Make prediction
        prediction = int(model.predict(X)[0])
        prediction_proba = model.predict_proba(X)[0]

        probability = float(prediction_proba[1])  # Probability of being cheater
        confidence = float(max(prediction_proba))

        # Determine risk level based on probability
        if probability >= 0.8:
            risk_level = "HIGH"
        elif probability >= 0.5:
            risk_level = "MEDIUM"
        elif probability >= 0.3:
            risk_level = "LOW"
        else:
            risk_level = "MINIMAL"

        result = {
            'prediction': prediction,
            'probability': probability,
            'confidence': confidence,
            'risk_level': risk_level
        }

        logger.info(f"Prediction: {result}")

        return jsonify(result), 200

    except Exception as e:
        logger.error(f"Prediction error: {e}", exc_info=True)
        return jsonify({'error': str(e)}), 500

@app.route('/model-info', methods=['GET'])
def model_info():
    config_path = os.path.join(MODEL_DIR, 'model_config.json')
    with open(config_path, 'r') as f:
        config = json.load(f)

    return jsonify(config), 200

@app.route('/features', methods=['GET'])
def get_features():
    return jsonify({
        'features': feature_names,
        'count': len(feature_names)
    }), 200


load_artifacts()

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=False)
