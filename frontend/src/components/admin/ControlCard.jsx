import { useState } from 'react';
import './ControlCard.css';

function ControlCard({ status, onStart, onStop, actionLoading }) {
  const [mode, setMode] = useState('RANDOM');
  const [batchSize, setBatchSize] = useState(10);

  const handleStart = () => {
    onStart(mode, batchSize);
  };

  return (
    <div className="dashboard-card">
      <h2>Data Gathering Control</h2>

      {!status?.running ? (
        <div className="control-form">
          <div className="form-row">
            <div className="form-field">
              <label htmlFor="mode">Gathering Mode:</label>
              <select
                id="mode"
                value={mode}
                onChange={(e) => setMode(e.target.value)}
                disabled={actionLoading}
              >
                <option value="RANDOM">Random</option>
                <option value="BANNED">Banned</option>
              </select>
            </div>

            <div className="form-field">
              <label htmlFor="batchSize">Batch Size:</label>
              <input
                type="number"
                id="batchSize"
                value={batchSize}
                onChange={(e) => setBatchSize(parseInt(e.target.value, 10))}
                min="1"
                max="50"
                disabled={actionLoading}
              />
            </div>
          </div>

          <button
            className="action-button start-button"
            onClick={handleStart}
            disabled={actionLoading}
          >
            {actionLoading ? 'Starting...' : 'Start Gathering'}
          </button>
        </div>
      ) : (
        <div className="control-actions">
          <p className="gathering-info">
            Data gathering is currently running in <strong>{status.mode}</strong> mode.
          </p>
          <button
            className="action-button stop-button"
            onClick={onStop}
            disabled={actionLoading}
          >
            {actionLoading ? 'Stopping...' : 'Stop Gathering'}
          </button>
        </div>
      )}
    </div>
  );
}

export default ControlCard;
