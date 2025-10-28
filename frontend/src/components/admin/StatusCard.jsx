import './StatusCard.css';

function StatusCard({ status }) {
  if (!status) return null;

  return (
    <div className="dashboard-card">
      <h2>Data Gathering Status</h2>

      <div className="status-grid">
        <div className="status-item">
          <span className="status-label">Status:</span>
          <span className={`status-value status-${status.running ? 'running' : 'stopped'}`}>
            {status.running ? 'RUNNING' : 'STOPPED'}
          </span>
        </div>

        {status.running && (
          <>
            <div className="status-item">
              <span className="status-label">Mode:</span>
              <span className="status-value">{status.mode}</span>
            </div>

            <div className="status-item">
              <span className="status-label">Batch Size:</span>
              <span className="status-value">{status.batchSize}</span>
            </div>

            <div className="status-item">
              <span className="status-label">Uptime:</span>
              <span className="status-value">{status.uptime || 0}s</span>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default StatusCard;
