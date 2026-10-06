import pandas as pd
import numpy as np
from pathlib import Path

# ==========================================
# CẤU HÌNH ĐƯỜNG DẪN ĐỘC LẬP MÔI TRƯỜNG
# ==========================================
BASE_DIR = Path(__file__).resolve().parent.parent
RAW_DIR = BASE_DIR / "raw"
DB_DIR = BASE_DIR / "database"

# Đảm bảo thư mục đích tồn tại
DB_DIR.mkdir(parents=True, exist_ok=True)

# 1. Đọc file gốc từ thư mục raw
raw_file_path = RAW_DIR / "Hotel Reservations.csv"
df = pd.read_csv(raw_file_path)

# Xử lý ngày nhận phòng: ghép arrival_year, arrival_month, arrival_date
def parse_arrival_date(row):
    try:
        return f"{int(row['arrival_year']):04d}-{int(row['arrival_month']):02d}-{int(row['arrival_date']):02d}"
    except Exception:
        return None

df['check_in_date'] = df.apply(parse_arrival_date, axis=1)

# Tính tổng số đêm
df['total_nights'] = df['no_of_weekend_nights'] + df['no_of_week_nights']
df['total_nights'] = df['total_nights'].replace(0, 1) # Nếu ở trong ngày tính là 1 đêm

# Tính ngày check-out
df['check_in_date_dt'] = pd.to_datetime(df['check_in_date'], errors='coerce')
df['check_out_date_dt'] = df['check_in_date_dt'] + pd.to_timedelta(df['total_nights'], unit='D')
df['check_out_date'] = df['check_out_date_dt'].dt.strftime('%Y-%m-%d')

# ==========================================
# 2. TÁCH BẢNG 1: room_types.csv
# ==========================================
raw_room_types = df.groupby('room_type_reserved').agg(
    base_price=('avg_price_per_room', 'mean')
).reset_index()

raw_room_types['id'] = range(1, len(raw_room_types) + 1)
raw_room_types['name'] = raw_room_types['room_type_reserved']
raw_room_types['base_price'] = raw_room_types['base_price'].round(2)
# Mặc định sức chứa mẫu theo từng loại phòng
raw_room_types['capacity'] = raw_room_types['name'].apply(
    lambda x: 4 if '7' in str(x) or '6' in str(x) else (3 if '4' in str(x) or '5' in str(x) else 2)
)
raw_room_types['description'] = raw_room_types['name'] + " - Tiêu chuẩn khách sạn quốc tế"

df_room_types = raw_room_types[['id', 'name', 'base_price', 'capacity', 'description']]
df_room_types.to_csv(DB_DIR / 'room_types_import.csv', index=False, encoding='utf-8')

# Map room_type_id ngược lại bảng chính
room_type_map = dict(zip(raw_room_types['name'], raw_room_types['id']))
df['room_type_id'] = df['room_type_reserved'].map(room_type_map)

# ==========================================
# 3. TÁCH BẢNG 2: rooms.csv (Sinh danh sách phòng mẫu)
# ==========================================
rooms = []
room_id = 1
for rt_id in raw_room_types['id']:
    # Mỗi loại phòng tạo ra 10 phòng cụ thể (ví dụ phòng 101-110, 201-210...)
    for r_num in range(1, 11):
        floor = rt_id
        room_number = f"{floor}{r_num:02d}"
        rooms.append({
            'id': room_id,
            'room_number': room_number,
            'room_type_id': rt_id,
            'status': 'AVAILABLE',
            'floor': floor
        })
        room_id += 1

df_rooms = pd.DataFrame(rooms)
df_rooms.to_csv(DB_DIR / 'rooms_import.csv', index=False, encoding='utf-8')

# ==========================================
# 4. TÁCH BẢNG 3: customers.csv
# ==========================================
customers = []
for idx, row in df.iterrows():
    cust_id = idx + 1
    full_name = f"Customer {row['Booking_ID']}"
    identity_card = f"ID{cust_id:08d}"
    phone = f"09{cust_id:08d}"[:10]
    email = f"guest_{cust_id}@hotelmail.com"
    country = "Vietnam"
    customers.append({
        'id': cust_id,
        'full_name': full_name,
        'identity_card': identity_card,
        'phone': phone,
        'email': email,
        'country': country
    })

df_customers = pd.DataFrame(customers)
df_customers.to_csv(DB_DIR / 'customers_import.csv', index=False, encoding='utf-8')
df['customer_id'] = range(1, len(df) + 1)

# ==========================================
# 5. TÁCH BẢNG 4: bookings.csv
# ==========================================
status_map = {
    'Not_Canceled': 'CONFIRMED',
    'Canceled': 'CANCELLED'
}
df['booking_status_db'] = df['booking_status'].map(status_map).fillna('CONFIRMED')

bookings = []
for idx, row in df.iterrows():
    b_id = idx + 1
    bookings.append({
        'id': b_id,
        'booking_code': row['Booking_ID'],
        'customer_id': row['customer_id'],
        'user_id': 1, # Nhân viên admin mặc định
        'status': row['booking_status_db'],
        'booking_date': row['check_in_date'] if pd.notnull(row['check_in_date']) else '2018-01-01',
        'total_deposit': 0.0
    })

df_bookings = pd.DataFrame(bookings)
df_bookings.to_csv(DB_DIR / 'bookings_import.csv', index=False, encoding='utf-8')

# ==========================================
# 6. TÁCH BẢNG 5: booking_details.csv
# ==========================================
booking_details = []
for idx, row in df.iterrows():
    b_id = idx + 1
    possible_rooms = df_rooms[df_rooms['room_type_id'] == row['room_type_id']]['id'].values
    selected_room_id = possible_rooms[idx % len(possible_rooms)] if len(possible_rooms) > 0 else 1
    
    check_in = row['check_in_date'] if pd.notnull(row['check_in_date']) else '2018-01-01'
    check_out = row['check_out_date'] if pd.notnull(row['check_out_date']) else '2018-01-02'
    
    booking_details.append({
        'id': b_id,
        'booking_id': b_id,
        'room_id': selected_room_id,
        'check_in_expected': check_in,
        'check_out_expected': check_out,
        'check_in_actual': check_in if row['booking_status_db'] == 'CONFIRMED' else None,
        'check_out_actual': check_out if row['booking_status_db'] == 'CONFIRMED' else None,
        'price_per_night': row['avg_price_per_room']
    })

df_booking_details = pd.DataFrame(booking_details)
df_booking_details.to_csv(DB_DIR / 'booking_details_import.csv', index=False, encoding='utf-8')

print("--> Đã bóc tách thành công và lưu trực tiếp vào thư mục 'database/'!")