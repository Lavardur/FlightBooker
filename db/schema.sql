DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Seat;
DROP TABLE IF EXISTS Flight;

CREATE TABLE Flight (
    flightNumber VARCHAR(5) PRIMARY KEY,
    origin VARCHAR(50),
    destination VARCHAR(50),
    departureTime VARCHAR(20),
    arrivalTime VARCHAR(20)
);

CREATE TABLE Seat (
    seatNumber INTEGER,
    seatStatus BOOLEAN,
    flightNumber VARCHAR(5),
    PRIMARY KEY (seatNumber, flightNumber),
    FOREIGN KEY (flightNumber) REFERENCES Flight(flightNumber)
);

CREATE TABLE Booking (
    bookingId VARCHAR(10) PRIMARY KEY,
    bookingDate VARCHAR(20),
    status VARCHAR(20),
    customerId VARCHAR(10),
    FOREIGN KEY (customerId) REFERENCES Customer(customerId)
);

CREATE TABLE Customer (
    customerId VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(7)
);