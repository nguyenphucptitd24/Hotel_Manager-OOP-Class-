import React, { useState, useEffect } from "react";
import api from "../services/api";
import "./DashboardPage.css";

const DashboardPage = () => {
    const [stats, setStats] = useState({
        totalRevenue: 0,
        occupancyRate: 0,
        cancellationRate: 0,
        totalBookings: 0,
        confirmedBookings: 0,
        cancelledBookings: 0,
        monthlyRevenue: []
    });
    const [loading, setLoading] = useState(true);

    const fetchStats = async () => {
        setLoading(true);
        try {
            const res = await api.get("/api/v1/analytics/dashboard");
            if (res.data) {
                setStats({
                    totalRevenue: res.data.totalRevenue ?? 0,
                    occupancyRate: res.data.occupancyRate ?? 0,
                    cancellationRate: res.data.cancellationRate ?? 0,
                    totalBookings: res.data.totalBookings ?? 0,
                    confirmedBookings: res.data.confirmedBookings ?? 0,
                    cancelledBookings: res.data.cancelledBookings ?? 0,
                    monthlyRevenue: res.data.monthlyRevenue || []
                });
            }
        } catch (err) {
            console.warn("Chưa kết nối được API thống kê hoặc dùng fallback:", err);
            setStats({
                totalRevenue: 7042183.1,
                occupancyRate: 67.2,
                cancellationRate: 32.8,
                totalBookings: 36275,
                confirmedBookings: 24390,
                cancelledBookings: 11885,
                monthlyRevenue: [
                    { month: "Tháng 1", revenue: 180000, bookingCount: 420 },
                    { month: "Tháng 2", revenue: 220000, bookingCount: 510 },
                    { month: "Tháng 3", revenue: 250000, bookingCount: 580 },
                    { month: "Tháng 4", revenue: 210000, bookingCount: 490 },
                    { month: "Tháng 5", revenue: 280000, bookingCount: 620 },
                    { month: "Tháng 6", revenue: 310000, bookingCount: 710 },
                ]
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStats();
    }, []);

    const maxRevenue = Math.max(...(stats.monthlyRevenue.map(m => m.revenue) || [1]), 1);
    const successRate = Number((100 - (stats.cancellationRate || 0)).toFixed(1));
    const cancelRate = Number((stats.cancellationRate || 0).toFixed(1));

    return (
        <div className="dashboard-page">
            {/* Header */}
            <div className="dashboard-page-header">
                <div>
                    <h1>Báo cáo & thống kê</h1>
                    <p className="subtitle">
                        Phân tích doanh thu, tỷ lệ lấp đầy, tỷ lệ hủy phòng và xu hướng đặt phòng theo tháng
                    </p>
                </div>
                <div className="dashboard-actions">
                    <button className="btn-secondary-action" onClick={fetchStats} disabled={loading}>
                        {loading ? "Đang đồng bộ..." : "Làm mới dữ liệu"}
                    </button>
                </div>
            </div>

            {/* KPI Summary Cards */}
            <div className="kpi-grid">
                <div className="kpi-card kpi-revenue">
                    <div className="kpi-header">
                        <span className="kpi-title">TỔNG DOANH THU</span>
                        <span className="kpi-badge">USD</span>
                    </div>
                    <div className="kpi-value text-green">
                        ${stats.totalRevenue ? stats.totalRevenue.toLocaleString() : 0}
                    </div>
                    <div className="kpi-footer">
                        <span>Doanh thu từ các đơn đặt phòng thành công</span>
                    </div>
                </div>

                <div className="kpi-card kpi-occupancy">
                    <div className="kpi-header">
                        <span className="kpi-title">TỶ LỆ LẤP ĐẦY PHÒNG</span>
                        <span className="kpi-badge">OCCUPANCY</span>
                    </div>
                    <div className="kpi-value text-blue">
                        {stats.occupancyRate}%
                    </div>
                    <div className="progress-bar-container">
                        <div
                            className="progress-bar-fill fill-blue"
                            style={{ width: `${Math.min(100, Math.max(0, stats.occupancyRate))}%` }}
                        />
                    </div>
                </div>

                <div className="kpi-card kpi-cancellation">
                    <div className="kpi-header">
                        <span className="kpi-title">TỶ LỆ HỦY PHÒNG</span>
                        <span className="kpi-badge bg-red">CANCELLATION</span>
                    </div>
                    <div className="kpi-value text-red">
                        {stats.cancellationRate}%
                    </div>
                    <div className="progress-bar-container">
                        <div
                            className="progress-bar-fill fill-red"
                            style={{ width: `${Math.min(100, Math.max(0, stats.cancellationRate))}%` }}
                        />
                    </div>
                </div>

                <div className="kpi-card kpi-bookings">
                    <div className="kpi-header">
                        <span className="kpi-title">TỔNG ĐƠN ĐẶT PHÒNG</span>
                        <span className="kpi-badge bg-purple">BOOKINGS</span>
                    </div>
                    <div className="kpi-value text-purple">
                        {stats.totalBookings ? stats.totalBookings.toLocaleString() : 0}
                    </div>
                    <div className="kpi-footer">
                        <span>Đã xác nhận: {stats.confirmedBookings?.toLocaleString() || 0} | Đã hủy: {stats.cancelledBookings?.toLocaleString() || 0}</span>
                    </div>
                </div>
            </div>

            {/* Charts Section */}
            <div className="charts-grid">
                {/* Monthly Revenue Bar Chart */}
                <div className="chart-card">
                    <div className="chart-header">
                        <h3>Biểu đồ doanh thu theo tháng</h3>
                        <span className="chart-legend-item">
                            <span className="legend-box box-blue"></span> Doanh thu (USD)
                        </span>
                    </div>

                    <div className="bar-chart-wrapper">
                        {stats.monthlyRevenue && stats.monthlyRevenue.length > 0 ? (
                            <div className="bar-chart">
                                {stats.monthlyRevenue.map((item, idx) => {
                                    const heightPercent = maxRevenue > 0 ? (item.revenue / maxRevenue) * 100 : 0;
                                    return (
                                        <div key={idx} className="bar-column">
                                            <div className="bar-tooltip">
                                                ${item.revenue?.toLocaleString()} ({item.bookingCount || 0} đơn)
                                            </div>
                                            <div className="bar-track">
                                                <div
                                                    className="bar-fill"
                                                    style={{ height: `${Math.max(8, heightPercent)}%` }}
                                                />
                                            </div>
                                            <span className="bar-label">{item.month}</span>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <p className="empty-text">Chưa có dữ liệu biểu đồ theo tháng.</p>
                        )}
                    </div>
                </div>

                {/* Donut / Ratio Card */}
                <div className="chart-card">
                    <div className="chart-header">
                        <h3>Phân bổ trạng thái đặt phòng</h3>
                    </div>

                    <div className="ratio-wrapper">
                        <div className="donut-visual">
                            <svg viewBox="0 0 36 36" className="circular-chart">
                                <path
                                    className="circle-bg"
                                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                                />
                                <path
                                    className="circle"
                                    strokeDasharray={`${successRate}, 100`}
                                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                                />
                                <text x="18" y="20.35" className="percentage">
                                    {successRate}%
                                </text>
                            </svg>
                        </div>

                        <div className="ratio-details">
                            <div className="ratio-item">
                                <div className="legend-indicator bg-green"></div>
                                <div className="ratio-info">
                                    <span className="ratio-name">Thành công / Lưu trú</span>
                                    <strong>{successRate}% ({stats.confirmedBookings?.toLocaleString() || 0} đơn)</strong>
                                </div>
                            </div>

                            <div className="ratio-item">
                                <div className="legend-indicator bg-red"></div>
                                <div className="ratio-info">
                                    <span className="ratio-name">Đã hủy (Canceled)</span>
                                    <strong>{cancelRate}% ({stats.cancelledBookings?.toLocaleString() || 0} đơn)</strong>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Monthly Details Table */}
            <div className="table-card">
                <div className="card-header-bar">
                    <h3>Chi tiết doanh số & đơn đặt theo tháng</h3>
                </div>
                <div className="table-responsive">
                    <table className="monthly-table">
                        <thead>
                            <tr>
                                <th>Thời gian</th>
                                <th>Số lượng booking</th>
                                <th>Doanh thu (USD)</th>
                                <th>Đóng góp doanh số</th>
                            </tr>
                        </thead>
                        <tbody>
                            {stats.monthlyRevenue && stats.monthlyRevenue.map((item, idx) => {
                                const share = stats.totalRevenue > 0
                                    ? ((item.revenue / stats.totalRevenue) * 100).toFixed(1)
                                    : "0.0";
                                return (
                                    <tr key={idx}>
                                        <td className="month-name">{item.month}</td>
                                        <td>{item.bookingCount ? item.bookingCount.toLocaleString() : "-"} đơn</td>
                                        <td className="rev-number">${item.revenue?.toLocaleString()}</td>
                                        <td>
                                            <div className="share-cell">
                                                <span>{share}%</span>
                                                <div className="share-bar">
                                                    <div className="share-bar-fill" style={{ width: `${Math.min(100, Number(share) * 3)}%` }}></div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default DashboardPage;
