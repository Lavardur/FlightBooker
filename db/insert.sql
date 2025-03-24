INSERT INTO Customer (customerId, name, email, phoneNumber)
VALUES 
('2404012070', 'Anton', 'anb59@hi.is', '1234567'),
('2404012071', 'Bjarki', 'bja34@hi.is', '7654321'),
('2404012072', 'Davíð', 'daf17@hi.is', '2345678');

INSERT INTO Flight (flightNumber, origin, destination, departureTime, arrivalTime)
VALUES
('FI101', 'KEF', 'JFK', '2020-11-01 12:00:00', '2020-11-01 18:00:00'),
('FI102', 'JFK', 'KEF', '2020-11-02 12:00:00', '2020-11-02 18:00:00'),
('FI103', 'KEF', 'LHR', '2020-11-03 12:00:00', '2020-11-03 18:00:00'),
('FI104', 'LHR', 'KEF', '2020-11-04 12:00:00', '2020-11-04 18:00:00');

INSERT INTO Seat (seatNumber, seatStatus, flightNumber)
VALUES
(1, 0, 'FI101'),
(2, 0, 'FI101'),
(3, 0, 'FI101'),
(4, 0, 'FI101'),
(5, 0, 'FI101'),
(6, 0, 'FI101'),
(7, 0, 'FI101'),
(8, 0, 'FI101'),
(9, 0, 'FI101'),
(10, 0, 'FI101'),
(1, 0, 'FI102'),
(2, 0, 'FI102'),
(3, 0, 'FI102'),
(4, 0, 'FI102'),
(5, 0, 'FI102'),
(6, 0, 'FI102'),
(7, 0, 'FI102'),
(8, 0, 'FI102'),
(9, 0, 'FI102'),
(10, 0, 'FI102'),
(1, 0, 'FI103'),
(2, 0, 'FI103'),
(3, 0, 'FI103'),
(4, 0, 'FI103'),
(5, 0, 'FI103'),
(6, 0, 'FI103'),
(7, 0, 'FI103'),
(8, 0, 'FI103'),
(9, 0, 'FI103'),
(10, 0, 'FI103'),
(1, 0, 'FI104'),
(2, 0, 'FI104'),
(3, 0, 'FI104'),
(4, 0, 'FI104'),
(5, 0, 'FI104'),
(6, 0, 'FI104'),
(7, 0, 'FI104'),
(8, 0, 'FI104'),
(9, 0, 'FI104'),
(10, 0, 'FI104');

INSERT INTO Booking (bookingId, bookingDate, status, customerId)
VALUES
('B101', '2020-10-01 12:00:00', 'CONFIRMED', '2404012070'),
('B102', '2020-10-02 12:00:00', 'CONFIRMED', '2404012071'),
('B103', '2020-10-03 12:00:00', 'CONFIRMED', '2404012072');