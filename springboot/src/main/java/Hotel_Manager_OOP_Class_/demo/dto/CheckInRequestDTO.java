package Hotel_Manager_OOP_Class.dto;

public class CheckInRequestDTO {
    private Long bookingId;
    private Long roomId;
    private String note;

    public CheckInRequestDTO() {}

    public CheckInRequestDTO(Long bookingId, Long roomId, String note) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.note = note;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}