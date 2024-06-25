
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS WinLoss (
    Username VARCHAR(255) PRIMARY KEY,
    Wins INT NOT NULL DEFAULT 0,
    Losses INT NOT NULL DEFAULT 0,
    WinLossPercentage FLOAT NOT NULL DEFAULT 0,
    FOREIGN KEY (Username) REFERENCES users(username)
);

CREATE TRIGGER update_win_loss_percentage_insert
AFTER INSERT ON WinLoss
FOR EACH ROW
BEGIN
  UPDATE WinLoss
  SET WinLossPercentage = CASE
                           WHEN NEW.Losses = 0 THEN 0
                           ELSE NEW.Wins * 1.0 / NEW.Losses
                         END
  WHERE Username = NEW.Username;
END;


CREATE TRIGGER update_win_loss_percentage_update
AFTER UPDATE ON WinLoss
FOR EACH ROW
BEGIN
  UPDATE WinLoss
  SET WinLossPercentage = CASE
                           WHEN NEW.Losses = 0 THEN 0
                           ELSE NEW.Wins * 1.0 / NEW.Losses
                         END
  WHERE Username = NEW.Username;
END;

