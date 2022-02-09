CREATE TABLE user (
      user_id VARCHAR(255) NOT NULL,
      balance  DECIMAL NOT NULL DEFAULT 0,

      PRIMARY KEY (user_id)
);

CREATE TABLE transactions (
     id int(11) NOT NULL AUTO_INCREMENT ,
     user_id VARCHAR(255) NOT NULL,
     balance  DECIMAL NOT NULL DEFAULT 0,
     transaction_id VARCHAR(255) NOT NULL,
     amount DECIMAL NOT NULL,
     transaction_type VARCHAR(255),
     created_at TIMESTAMP,

     PRIMARY KEY (id),
     CONSTRAINT `transaction` FOREIGN KEY (user_id) REFERENCES user(user_id)
);

INSERT INTO user ( user_id, balance)
VALUES ('f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454', 0.00);

INSERT INTO user ( user_id, balance)
VALUES ('f8c3de3d-1fea-4d7c-a8b0-29f63c4c3451', 0.00);

INSERT INTO transactions (user_id, balance, transaction_id, amount)
VALUES ('f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454', 0.00, 'f8c3de3d-1fea-4d7c-a8b0-29f63c4c3452', 0.00);

INSERT INTO transactions (user_id, balance, transaction_id, amount)
VALUES ('f8c3de3d-1fea-4d7c-a8b0-29f63c4c3451', 0.00, 'f8c2de3d-1fea-4d7c-a8b0-29f63c444452', 0.00);

