INSERT INTO color(name, color_code) VALUES ('Đỏ', '#FF0000');
INSERT INTO color(name, color_code) VALUES ('Xanh lam', '#0000FF');
INSERT INTO color(name, color_code) VALUES ('Xanh lục', '#00FF00');
INSERT INTO color(name, color_code) VALUES ('Vàng', '#FFFF00');
INSERT INTO color(name, color_code) VALUES ('Tím', '#800080');
INSERT INTO color(name, color_code) VALUES ('Cam', '#FFA500');
INSERT INTO color(name, color_code) VALUES ('Hồng', '#FFC0CB');

INSERT INTO size(name) VALUES ('Nhỏ');
INSERT INTO size(name) VALUES ('Trung bình');
INSERT INTO size(name) VALUES ('Lớn');

INSERT INTO material(name) VALUES('Gỗ');
INSERT INTO material(name) VALUES('Thủy tinh');
INSERT INTO material(name) VALUES('Bạc');

INSERT INTO category(name, parent_id) VALUES('Phòng ngủ', NULL);
SET @parentIdPhòngngủ = LAST_INSERT_ID();

INSERT INTO category(name, parent_id) VALUES('Phòng bếp', NULL);
SET @parentIdPhòngbếp = LAST_INSERT_ID();

INSERT INTO category(name, parent_id) VALUES('Phòng tắm', NULL);
SET @parentIdPhòngtắm = LAST_INSERT_ID();

INSERT INTO category(name, parent_id) VALUES('Phòng khách', NULL);
SET @parentIdPhòngkhách = LAST_INSERT_ID();

INSERT INTO category(name, parent_id) VALUES('Đèn ngủ', @parentIdPhòngngủ);
INSERT INTO category(name, parent_id) VALUES('Giường', @parentIdPhòngngủ);
INSERT INTO category(name, parent_id) VALUES('Tủ chén', @parentIdPhòngbếp);
INSERT INTO category(name, parent_id) VALUES('Ghế phòng ăn', @parentIdPhòngbếp);
INSERT INTO category(name, parent_id) VALUES('Chậu rửa mặt', @parentIdPhòngtắm);
INSERT INTO category(name, parent_id) VALUES('Bồn tắm', @parentIdPhòngtắm);
INSERT INTO category(name, parent_id) VALUES('Đèn để bàn', @parentIdPhòngkhách);
INSERT INTO category(name, parent_id) VALUES('Ghế sofa', @parentIdPhòngkhách);











