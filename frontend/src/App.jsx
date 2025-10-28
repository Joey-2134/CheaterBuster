import {useEffect, useState} from 'react'
import './App.css'
import {getPlayerCount} from "./request/gathering.js";

function App() {
  const [count, setCount] = useState(0)

    useEffect(() => {
        getPlayerCount()
            .then(data => setCount(data))
            .catch(err => console.error('Failed to fetch count:', err));
    }, []);


    return (
    <>
      <h1>CheaterBuster</h1>
      <div className="card">
          <p>This model has been trained on</p>
            {count}
          <p>different players!</p>
      </div>
    </>
  )
}

export default App
