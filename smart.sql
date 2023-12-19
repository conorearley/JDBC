
DROP DATABASE IF EXISTS smart;

CREATE DATABASE smart;

USE smart;

CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY AUTO_INCREMENT,
    RoomName VARCHAR(255) NOT NULL
);

CREATE TABLE Devices (
    DeviceID INT PRIMARY KEY AUTO_INCREMENT,
    DeviceName VARCHAR(255) NOT NULL,
    DeviceType VARCHAR(50) NOT NULL,
    Status VARCHAR(20) DEFAULT 'Offline',
    RoomID INT,
    FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID)
);

CREATE TABLE Readings (
    ReadingID INT PRIMARY KEY AUTO_INCREMENT,
    DeviceID INT,
    ReadingType VARCHAR(50) NOT NULL,
    Value DOUBLE NOT NULL,
    ReadingTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (DeviceID) REFERENCES Devices(DeviceID)
);
INSERT INTO Rooms (RoomName) VALUES
    ('Living Room'), #1
    ('Bedroom'), #2
    ('Kitchen'), #3
    ('Bathroom'), #4
    ('Office'), #5
	('Guest Room'); #6

INSERT INTO Devices (DeviceName, DeviceType, RoomID) VALUES
    ('Smart TV', 'Entertainment', 1),
    ('Smart Thermostat', 'Climate Control', 2),
    ('Smart Light Bulb', 'Lighting', 4),
    ('Security Camera', 'Security', 5),
    ('Smart Speaker', 'Entertainment', 1),
    ('Smart Refrigerator', 'Appliance', 3),
    ('Smart Oven', 'Appliance', 3),
    ('Smart Coffee Maker', 'Appliance', 3),
    ('Smart Desk Lamp', 'Lighting', 6);

INSERT INTO Readings (DeviceID, ReadingType, Value, ReadingTime) VALUES
    (1, 'Power Consumption', 50, '2023-01-15 08:30:00'),
    (2, 'Temperature', 22.5, '2023-01-15 09:15:00'),
    (3, 'Brightness', 75, '2023-01-15 10:00:00'),
    (4, 'Motion', 1, '2023-01-15 11:45:00'),
    (5, 'Volume Level', 60, '2023-01-15 12:30:00'),
    (6, 'Temperature', 4.0, '2023-01-15 14:00:00'),
    (7, 'Temperature', 180.0, '2023-01-15 14:45:00'),
    (8, 'Coffee Level', 0.5, '2023-01-15 15:30:00'),
    (9, 'Brightness', 90, '2023-01-15 16:15:00'),
    (1, 'Power Consumption', 78, '2023-01-15 09:30:00'),
    (2, 'Temperature', 24, '2023-01-15 10:15:00'),
    (4, 'Motion', 0, '2023-01-15 12:00:00'),
    (5, 'Volume Level', 0, '2023-01-15 13:30:00'),
    (6, 'Temperature', 2.0, '2023-01-15 15:00:00'),
    (7, 'Temperature', 230.0, '2023-01-15 15:25:00'),
    (8, 'Coffee Level', 0.7, '2023-01-15 16:30:00'),
    (9, 'Brightness', 0, '2023-01-15 17:15:00');
    select * from devices;
    select * from readings;
    select * from rooms;
    
    DELIMITER //

CREATE PROCEDURE GetReadingsByRoom(
    IN p_RoomName VARCHAR(255)
)
BEGIN
    SELECT R.DeviceName, RD.ReadingType, RD.Value, RD.ReadingTime
    FROM Rooms AS RM
    JOIN Devices AS R ON RM.RoomID = R.RoomID
    JOIN Readings AS RD ON R.DeviceID = RD.DeviceID
    WHERE RM.RoomName = p_RoomName;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE GetReadingsForDevice(IN pDeviceID INT)
BEGIN
    SELECT *
    FROM Readings
    WHERE DeviceID = pDeviceID;
END //

DELIMITER ;

CALL GetReadingsByRoom('Living Room');
CALL GetReadingsForDevice(1);
    
