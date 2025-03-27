-- Clear existing data if needed
-- DELETE FROM Booking;
-- DELETE FROM Seat;
-- DELETE FROM Flight;

INSERT INTO Customer (customerId, name, email, phoneNumber)
VALUES 
('2404012070', 'Anton', 'anb59@hi.is', '1234567'),
('2404012071', 'Bjarki', 'bja34@hi.is', '7654321'),
('2404012072', 'Davíð', 'daf17@hi.is', '2345678'),
('2404012073', 'Elías', 'eli22@hi.is', '3456789'),
('2404012074', 'Fríða', 'fri15@hi.is', '4567890');

-- Using date functions to create flights relative to today with unique flight numbers
INSERT INTO Flight (flightNumber, origin, destination, departureTime, arrivalTime)
VALUES
-- Today
('FI101', 'KEF', 'JFK', datetime('now'), datetime('now', '+6 hours')),
('FI102', 'JFK', 'KEF', datetime('now', '+2 hours'), datetime('now', '+8 hours')),
('FI201', 'KEF', 'CPH', datetime('now', '+1 hour'), datetime('now', '+3 hours, 30 minutes')),
('FI202', 'CPH', 'KEF', datetime('now', '+3 hours'), datetime('now', '+5 hours, 30 minutes')),
('FI301', 'KEF', 'OSL', datetime('now', '+4 hours'), datetime('now', '+6 hours, 15 minutes')),
('FI302', 'OSL', 'KEF', datetime('now', '+6 hours'), datetime('now', '+8 hours, 15 minutes')),

-- Tomorrow
('FI103', 'KEF', 'LHR', datetime('now', '+1 day'), datetime('now', '+1 day', '+3 hours, 30 minutes')),
('FI104', 'LHR', 'KEF', datetime('now', '+1 day', '+5 hours'), datetime('now', '+1 day', '+8 hours, 30 minutes')),
('FI203', 'KEF', 'AMS', datetime('now', '+1 day', '+2 hours'), datetime('now', '+1 day', '+4 hours, 45 minutes')),
('FI204', 'AMS', 'KEF', datetime('now', '+1 day', '+6 hours'), datetime('now', '+1 day', '+8 hours, 45 minutes')),
('FI303', 'KEF', 'BER', datetime('now', '+1 day', '+3 hours'), datetime('now', '+1 day', '+5 hours, 30 minutes')),
('FI304', 'BER', 'KEF', datetime('now', '+1 day', '+7 hours'), datetime('now', '+1 day', '+9 hours, 30 minutes')),

-- Day after tomorrow
('FI105', 'KEF', 'CDG', datetime('now', '+2 days'), datetime('now', '+2 days', '+3 hours')),
('FI106', 'CDG', 'KEF', datetime('now', '+2 days', '+4 hours'), datetime('now', '+2 days', '+7 hours')),
('FI205', 'KEF', 'MAD', datetime('now', '+2 days', '+1 hour'), datetime('now', '+2 days', '+4 hours, 30 minutes')),
('FI206', 'MAD', 'KEF', datetime('now', '+2 days', '+5 hours'), datetime('now', '+2 days', '+8 hours, 30 minutes')),
('FI305', 'KEF', 'FCO', datetime('now', '+2 days', '+2 hours'), datetime('now', '+2 days', '+5 hours')),
('FI306', 'FCO', 'KEF', datetime('now', '+2 days', '+6 hours'), datetime('now', '+2 days', '+9 hours')),

-- This weekend (3-4 days from now)
('FI107', 'KEF', 'ARN', datetime('now', '+3 days'), datetime('now', '+3 days', '+2 hours, 45 minutes')),
('FI108', 'ARN', 'KEF', datetime('now', '+3 days', '+4 hours'), datetime('now', '+3 days', '+6 hours, 45 minutes')),
('FI207', 'KEF', 'HEL', datetime('now', '+4 days'), datetime('now', '+4 days', '+3 hours')),
('FI208', 'HEL', 'KEF', datetime('now', '+4 days', '+4 hours'), datetime('now', '+4 days', '+7 hours')),

-- Next week
('FI109', 'KEF', 'CPH', datetime('now', '+7 days'), datetime('now', '+7 days', '+3 hours, 30 minutes')),
('FI110', 'CPH', 'KEF', datetime('now', '+7 days', '+5 hours'), datetime('now', '+7 days', '+8 hours, 30 minutes')),
('FI209', 'KEF', 'LHR', datetime('now', '+7 days', '+2 hours'), datetime('now', '+7 days', '+4 hours, 45 minutes')),
('FI210', 'LHR', 'KEF', datetime('now', '+7 days', '+6 hours'), datetime('now', '+7 days', '+8 hours, 45 minutes')),
('FI307', 'KEF', 'JFK', datetime('now', '+7 days', '+1 hour'), datetime('now', '+7 days', '+7 hours')),
('FI308', 'JFK', 'KEF', datetime('now', '+7 days', '+9 hours'), datetime('now', '+7 days', '+15 hours')),

-- 2 weeks from now
('FI111', 'KEF', 'BOS', datetime('now', '+14 days'), datetime('now', '+14 days', '+5 hours, 45 minutes')),
('FI112', 'BOS', 'KEF', datetime('now', '+14 days', '+7 hours'), datetime('now', '+14 days', '+12 hours, 45 minutes')),
('FI211', 'KEF', 'ORD', datetime('now', '+14 days', '+2 hours'), datetime('now', '+14 days', '+8 hours')),
('FI212', 'ORD', 'KEF', datetime('now', '+14 days', '+10 hours'), datetime('now', '+14 days', '+16 hours')),

-- 3 weeks from now
('FI113', 'KEF', 'YYZ', datetime('now', '+21 days'), datetime('now', '+21 days', '+6 hours')),
('FI114', 'YYZ', 'KEF', datetime('now', '+21 days', '+8 hours'), datetime('now', '+21 days', '+14 hours')),

-- 1 month from now
('FI115', 'KEF', 'SFO', datetime('now', '+30 days'), datetime('now', '+30 days', '+9 hours')),
('FI116', 'SFO', 'KEF', datetime('now', '+30 days', '+11 hours'), datetime('now', '+30 days', '+20 hours')),
('FI213', 'KEF', 'DXB', datetime('now', '+31 days'), datetime('now', '+31 days', '+7 hours')),
('FI214', 'DXB', 'KEF', datetime('now', '+31 days', '+9 hours'), datetime('now', '+31 days', '+16 hours'));

-- Helper to generate seat inserts for all flights
WITH FlightNumbers AS (
  SELECT DISTINCT flightNumber FROM Flight
)
INSERT INTO Seat (seatNumber, seatStatus, flightNumber)
SELECT 
  seatNum, 
  0, 
  fn.flightNumber
FROM 
  FlightNumbers fn,
(
    SELECT 'A1' AS seatNum UNION SELECT 'A2' UNION SELECT 'A3' UNION SELECT 'A4' UNION SELECT 'A5' UNION SELECT 'A6' UNION SELECT 'A7' UNION SELECT 'A8' UNION SELECT 'A9' UNION SELECT 'A10' UNION
    SELECT 'B1' UNION SELECT 'B2' UNION SELECT 'B3' UNION SELECT 'B4' UNION SELECT 'B5' UNION SELECT 'B6' UNION SELECT 'B7' UNION SELECT 'B8' UNION SELECT 'B9' UNION SELECT 'B10' UNION
    SELECT 'C1' UNION SELECT 'C2' UNION SELECT 'C3' UNION SELECT 'C4' UNION SELECT 'C5' UNION SELECT 'C6' UNION SELECT 'C7' UNION SELECT 'C8' UNION SELECT 'C9' UNION SELECT 'C10' UNION
    SELECT 'D1' UNION SELECT 'D2' UNION SELECT 'D3' UNION SELECT 'D4' UNION SELECT 'D5' UNION SELECT 'D6' UNION SELECT 'D7' UNION SELECT 'D8' UNION SELECT 'D9' UNION SELECT 'D10'
) seats;

-- Insert bookings with dates relative to now
INSERT INTO Booking (bookingId, bookingDate, status, customerId, flightNumber, seatNumber)
VALUES
('B101', datetime('now', '-3 days'), 'CONFIRMED', '2404012070', 'FI101', 'A1'),
('B102', datetime('now', '-2 days'), 'CONFIRMED', '2404012071', 'FI101', 'A2'),
('B103', datetime('now', '-1 day'), 'CONFIRMED', '2404012072', 'FI102', 'A1'),
('B104', datetime('now', '-5 days'), 'CONFIRMED', '2404012073', 'FI103', 'B3'),
('B105', datetime('now', '-4 days'), 'CONFIRMED', '2404012074', 'FI201', 'C5'),
('B106', datetime('now', '-2 days'), 'CONFIRMED', '2404012070', 'FI202', 'D7'),
('B107', datetime('now', '-1 day'), 'CONFIRMED', '2404012071', 'FI301', 'A4'),
('B108', datetime('now', '-3 days'), 'CONFIRMED', '2404012072', 'FI107', 'B8'),
('B109', datetime('now', '-6 days'), 'CONFIRMED', '2404012073', 'FI109', 'C2'),
('B110', datetime('now', '-5 days'), 'CONFIRMED', '2404012074', 'FI111', 'D4');

-- Update corresponding seats to reflect they are booked
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI101' AND seatNumber = 'A1';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI101' AND seatNumber = 'A2';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI102' AND seatNumber = 'A1';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI103' AND seatNumber = 'B3';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI201' AND seatNumber = 'C5';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI202' AND seatNumber = 'D7';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI301' AND seatNumber = 'A4';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI107' AND seatNumber = 'B8';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI109' AND seatNumber = 'C2';
UPDATE Seat SET seatStatus = 1 WHERE flightNumber = 'FI111' AND seatNumber = 'D4';