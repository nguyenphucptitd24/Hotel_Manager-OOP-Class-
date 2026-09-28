import React, { useState } from 'react';
import ReceptionPage from './ReceptionPage';
import DashboardPage from './DashboardPage';

function App() {
  const [tab, setTab] = useState('reception');

  return (
    <div>
      <div style={{ backgroundColor: '#1a237e', padding: '12px 24px', display: 'flex', gap: '16px' }}>
        <button 
          onClick={() => setTab('reception')}
          style={{ 
            padding: '8px 16px', 
            border: 'none', 
            borderRadius: '4px', 
            cursor: 'pointer', 
            fontWeight: 'bold', 
            backgroundColor: tab === 'reception' ? '#fff' : 'transparent', 
            color: tab === 'reception' ? '#1a237e' : '#fff' 
          }}>
          📋 Nghiệp Vụ Lễ Tân
        </button>
        <button 
          onClick={() => setTab('dashboard')}
          style={{ 
            padding: '8px 16px', 
            border: 'none', 
            borderRadius: '4px', 
            cursor: 'pointer', 
            fontWeight: 'bold', 
            backgroundColor: tab === 'dashboard' ? '#fff' : 'transparent', 
            color: tab === 'dashboard' ? '#1a237e' : '#fff' 
          }}>
          📊 Dashboard Báo Cáo
        </button>
      </div>

      {tab === 'reception' ? <ReceptionPage /> : <DashboardPage />}
    </div>
  );
}

export default App;