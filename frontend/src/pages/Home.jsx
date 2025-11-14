import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayerCount } from "../request/gathering.js";
import { predictPlayer } from "../request/model.js";
import './Home.css';

function Home() {
  const [count, setCount] = useState(0);
  const [steamId, setSteamId] = useState('');
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    getPlayerCount()
      .then(data => setCount(data))
      .catch(err => console.error('Failed to fetch count:', err));
  }, []);

  const handlePredict = async (e) => {
    e.preventDefault();
    if (!steamId.trim()) {
      setError('Please enter a Steam ID');
      return;
    }

    setLoading(true);
    setError(null);
    setPrediction(null);

    try {
      const result = await predictPlayer(steamId);
      setPrediction(result);
    } catch (err) {
      setError('Failed to get prediction. Please check the Steam ID and try again.');
      console.error('Prediction error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home-container">
      <nav className="navbar">
        <h1>CheaterBuster</h1>
        <button onClick={() => navigate('/admin')} className="admin-button">
          Admin Panel
        </button>
      </nav>

      <div className="content">
        <form onSubmit={handlePredict} className="predict-form">
          <input
            type="text"
            value={steamId}
            onChange={(e) => setSteamId(e.target.value)}
            placeholder="Enter Steam ID"
            className="steam-id-input"
            disabled={loading}
          />
          <button type="submit" disabled={loading} className="predict-button">
            {loading ? 'Analyzing...' : 'Check Player'}
          </button>
        </form>

        {error && <div className="error-message">{error}</div>}

        {prediction && (
          <div className="prediction-result">
            <h2>Prediction Result</h2>
            <div className="result-details">
              <div className="result-item">
                <span className="result-label">Status:</span>
                <span className={`result-value ${prediction.prediction === 1 ? 'cheater' : 'legit'}`}>
                  {prediction.prediction === 1 ? 'Likely Cheater' : 'Likely Legit'}
                </span>
              </div>
              <div className="result-item">
                <span className="result-label">Risk Level:</span>
                <span className={`risk-level ${prediction.riskLevel?.toLowerCase()}`}>
                  {prediction.riskLevel || 'N/A'}
                </span>
              </div>
              <div className="result-item">
                <span className="result-label">Probability:</span>
                <span className="result-value">
                  {(prediction.probability * 100).toFixed(2)}%
                </span>
              </div>
              <div className="result-item">
                <span className="result-label">Confidence:</span>
                <span className="result-value">
                  {(prediction.confidence * 100).toFixed(2)}%
                </span>
              </div>
            </div>
          </div>
        )}

        <div className="card">
          <p>This model has been trained on</p>
          <div className="count">{count}</div>
          <p>different players!</p>
        </div>
      </div>
    </div>
  );
}

export default Home;
