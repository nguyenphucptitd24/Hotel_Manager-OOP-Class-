import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

const DashboardPage = () => {
  const [stats, setStats] = useState({
    totalRevenue: 0,
    occupancyRate: 0,
    cancellationRate: 0,
    monthlyRevenue: []
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/analytics/dashboard');
        if (response.data) {
          setStats({
            totalRevenue: response.data.totalRevenue ?? 0,
            occupancyRate: response.data.occupancyRate ?? 0,
            cancellationRate: response.data.cancellationRate ?? 0,
            monthlyRevenue: response.data.monthlyRevenue || []
          });
        }
      } catch (error) {
        console.warn("Chưa kết nối được Backend Spring Boot (/api/v1/analytics/dashboard).", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const pieData = [
    { name: 'Hoàn tất (Not Canceled)', value: Number((100 - (stats.cancellationRate || 0)).toFixed(1)) },
    { name: 'Đã hủy (Canceled)', value: Number((stats.cancellationRate || 0).toFixed(1)) },
  ];
  
  const COLORS = ['#2e7d32', '#d32f2f'];

  return (
    <div style={{ padding: '24px', fontFamily: 'Segoe UI, sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h2 style={{ color: '#1a237e', margin: 0 }}>📊 Dashboard Báo Cáo & Analytics (Hệ Thống Khách Sạn)</h2>
        <span style={{ fontSize: '13px', color: '#666', backgroundColor: '#e8eaf6', padding: '6px 12px', borderRadius: '16px' }}>
          {loading ? '🔄 Đang đồng bộ...' : '🟢 Dữ liệu thời gian thực'}
        </span>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px', marginBottom: '30px' }}>
        <div style={{ padding: '20px', background: '#e8f5e9', borderRadius: '8px', borderLeft: '6px solid #2e7d32' }}>
          <span style={{ color: '#2e7d32', fontWeight: 'bold', fontSize: '13px' }}>TỔNG DOANH THU (REVENUE)</span>
          <h1 style={{ margin: '8px 0 0 0', color: '#1b5e20' }}>
            ${stats.totalRevenue ? stats.totalRevenue.toLocaleString() : 0}
          </h1>
        </div>

        <div style={{ padding: '20px', background: '#e3f2fd', borderRadius: '8px', borderLeft: '6px solid #1565c0' }}>
          <span style={{ color: '#1565c0', fontWeight: 'bold', fontSize: '13px' }}>TỶ LỆ LẤP ĐẦY (OCCUPANCY)</span>
          <h1 style={{ margin: '8px 0 0 0', color: '#0d47a1' }}>
            {stats.occupancyRate}%
          </h1>
        </div>

        <div style={{ padding: '20px', background: '#ffebee', borderRadius: '8px', borderLeft: '6px solid #c62828' }}>
          <span style={{ color: '#c62828', fontWeight: 'bold', fontSize: '13px' }}>TỶ LỆ HỦY ĐƠN (CANCELLATION)</span>
          <h1 style={{ margin: '8px 0 0 0', color: '#b71c1c' }}>
            {stats.cancellationRate}%
          </h1>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <h3 style={{ marginBottom: '16px', color: '#333', fontSize: '16px' }}>Doanh Thu Theo Tháng (USD)</h3>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={stats.monthlyRevenue}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => [`$${value.toLocaleString()}`, 'Doanh thu']} />
                <Bar dataKey="revenue" fill="#1565c0" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div style={{ backgroundColor: '#fff', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <h3 style={{ marginBottom: '16px', color: '#333', fontSize: '16px' }}>Phân Tỷ Lệ Đặt Phòng thành công / Hủy</h3>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label={({ name, percent }) => `${(percent * 100).toFixed(0)}%`}>
                  {pieData.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => [`${value}%`, 'Tỷ lệ']} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;