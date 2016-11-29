BEGIN;

USE currency_app;

INSERT INTO role (id, role_name) VALUES
  (1000, 'ROLE_ANONYMOUS'),
  (1001, 'ROLE_USER'),
  (1002, 'ROLE_ADMIN');

INSERT INTO user (id, username, password) VALUES (11, 'admin', ?);
INSERT INTO user_details (user_id, first_name, last_name, email) VALUES (11, 'Ilya', 'Potapchuk', 'ilya_potapchuk@mail.ru');
INSERT INTO user_role (user_id, role_id) VALUES (11, 1002);
INSERT INTO user_preferences (user_id, ui_notifications, mail_notifications, theme_name, two_factor_auth, email_sign_in)  VALUES (11, 1, 0, 'valo-facebook', 0, 1);

COMMIT;
