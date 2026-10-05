import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

const DashboardPage = () => {
  const [stats, setStats] = useState({
    totalRevenue: 1285000,
    occupancyRate: 67.2,
    cancellationRate: 32.8,
    monthlyRevenue: [
      { month: 'Tháng 1', revenue: 180000 },
      { month: 'Tháng 2', revenue: 220000 },
      { month: 'Tháng 3', revenue: 250000 },
      { month: 'Tháng 4', revenue: 210000 },
      { month: 'Tháng 5', revenue: 280000 },
      { month: 'Tháng 6', revenue: 310000 },
    ]
  });

  useEffect(() => {
    axios.get('http://localhost:8080/api/v1/analytics/dashboard')
      .then(response => {
        if (response.data) {
          setStats(response.data);
        }
      })
      .catch(error => {
        console.log("Đang dùng dữ liệu thử nghiệm (Backend chưa bật hoặc chưa kết nối):", error);
      });
  }, []);

  const pieData = [
    { name: 'Thành công (Not Canceled)', value: 100 - stats.cancellationRate },
    { name: 'Đã hủy (Canceled)', value: stats.cancellationRate },
  ];
  const COLORS = ['#2e7d32', '#d32f2f'];

  return (
    <div style={{ padding: '24px', fontFamily: 'Segoe UI, sans-serif' }}>
      <h2 style={{ color: '#1a237e', marginBottom: '24px' }}>📊 Dashboard Báo Cáo & Analytics (Dữ liệu Kaggle)</h2>
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px', marginBottom: '30px' }}>
        <div style={{ padding: '20px', background: '#e8f5e9', borderRadius: '8px', borderLeft: '6px solid #2e7d32' }}>
          <span style={{ color: '#2e7d32', fontWeight: 'bold' }}>TỔNG DOANH THU</span>
          <h1 style={{ margin: '8px 0 0 0', color: '#1b5e20' }}>${stats.totalRevenue ? stats.totalRevenue.toLocaleString() : 0}</h1>
        </div>
        <div style={{ padding: '20px', background: '#e3f2fd', borderRadius: '8px', borderLeft: '6px solid #1565c0' }}>
          <span style={{ color: '#1565c0', fontWeight: 'bold' }}>TỶ LỆ LẤP ĐẦY PHÒNG</span>
          <h1 style={{ margin: '8px 0 0 0', color: '#0d47a1' }}>{stats.occupancyRate}%</h1>
        </div>
        <div style={{ padding: '20px', background: '#ffebee', borderRadius: '8px', borderLeft: '6px solid #c62828' }}>
          <span style={{ color: '#c62828', fontWeight: 'bold' }}>TỶ LỆ HỦY ĐƠN (CANCELED)</span>
          <h1 style={{ margin: '8px 0 0 0', color: '#b71c1c' }}>{stats.cancellationRate}%</h1>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <h3 style={{ marginBottom: '16px', color: '#333' }}>Doanh Thu Theo Tháng (USD)</h3>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={stats.monthlyRevenue}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="revenue" fill="#1565c0" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <h3 style={{ marginBottom: '16px', color: '#333' }}>Tỷ Lệ Hủy vs Hoàn Tất</h3>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label>
                  {pieData.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;