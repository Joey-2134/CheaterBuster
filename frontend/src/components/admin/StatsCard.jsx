import { useState, useEffect } from 'react';
import { getPlayerCount } from '../../request/gathering.js';
import './StatsCard.css';

function StatsCard() {
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    getPlayerCount()
      .then(data => setTotalCount(data))
      .catch(err => console.error('Failed to fetch count:', err));

    const interval = setInterval(() => {
      getPlayerCount()
        .then(data => setTotalCount(data))
        .catch(err => console.error('Failed to fetch count:', err));
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="dashboard-card">
      <h2>Total Statistics</h2>
      <div className="stats-grid">
        <div className="stat-box">
          <div className="stat-value">{totalCount}</div>
          <div className="stat-label">Total in Database</div>
        </div>
      </div>
    </div>
  );
}

export default StatsCard;
