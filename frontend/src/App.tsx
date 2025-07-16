import React, { useState } from 'react';
import './App.css';
import { AuthProvider } from './context/AuthContext';
import LoginTab from './components/LoginTab';
import CalendarTab from './components/CalendarTab';

function App() {
  const [tab, setTab] = useState<'login' | 'calendar'>('login');

  return (
    <AuthProvider>
      <div className="App">
        <header className="App-header">
          <h1>Calendar Aggregator</h1>
          <div style={{ marginBottom: 20 }}>
            <button onClick={() => setTab('login')} disabled={tab === 'login'}>Login</button>
            <button onClick={() => setTab('calendar')} disabled={tab === 'calendar'}>Calendar</button>
          </div>
          {tab === 'login' && <LoginTab />}
          {tab === 'calendar' && <CalendarTab />}
        </header>
      </div>
    </AuthProvider>
  );
}

export default App; 