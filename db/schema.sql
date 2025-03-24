/*

SQLITE3 - Original Schema

Flight
- flightNumber:    String
- origin:    String
- destination:    String
- departureTime:    DateTime
- arrivalTime:    DateTime
- seats:    List<Seat>

Seat
- seatNumber:    Int
- seatStatus:    SeatStatus
- flight: Flight

Booking
- bookingId: String
- bookingDate: DateTime
- status: BookingStatus
- customer: Customer
- seatList: List<seat>

Customer
- customerId: String
- name: String
- email: String
- phoneNumber: String

*/

DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Seat;
DROP TABLE IF EXISTS Flight;

CREATE TABLE Flight (
    flightNumber TEXT PRIMARY KEY,
    origin TEXT,
    destination TEXT,
    departureTime TEXT,
    arrivalTime TEXT
);

CREATE TABLE Seat (
    seatNumber INTEGER PRIMARY KEY,
    seatStatus BOOLEAN,
    flightNumber TEXT,
    FOREIGN KEY (flightNumber) REFERENCES Flight(flightNumber)
);

CREATE TABLE Booking (
    bookingId TEXT PRIMARY KEY,
    bookingDate TEXT,
    status TEXT,
    customerId TEXT,
    FOREIGN KEY (customerId) REFERENCES Customer(customerId)
);

CREATE TABLE Customer (
    customerId TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    phoneNumber TEXT
);