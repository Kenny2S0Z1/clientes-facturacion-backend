

INSERT INTO regiones (id,nombre) VALUES (1,'Sudamérica');
INSERT INTO regiones (id,nombre) VALUES (2,'Europa');

INSERT INTO clientes (nombre,apellido,email,create_at,foto,region_id) VALUES('Andres','Laporta','andres@gmail.com','2018-01-01','',1);
INSERT INTO clientes (nombre,apellido,email,create_at,foto,region_id) VALUES('Kenny','Silva','silvazambranokennyandres@gmail.com','2018-01-01','',1);
INSERT INTO clientes (nombre,apellido,email,create_at,foto,region_id) VALUES('Linus','Torvalds','linux@gmail.com','2018-01-01','',2);


INSERT INTO usuarios(username,nombre,apellido,email,password,enabled) VALUES ('andres','guzman','perez','andres@gmail.com','$2a$10$wiZc8wskizOMmAzD6joLrukB3Dj1soDdszNR.KL1lyI1tIqXV/Q8y',1);
INSERT INTO usuarios(username,nombre,apellido,email,password,enabled) VALUES ('admin','florencio','joaquin','admin123@gmail.com','$2a$10$raYrlHTfb/9VjcXRKw5kvee/hB9uJNeNX4hIY4d7UKfXmEpIf0FkW',1);



INSERT INTO roles(nombre) VALUES ('ROLE_USER');
INSERT INTO roles(nombre) VALUES ('ROLE_ADMIN');

INSERT INTO usuarios_roles (usuario_id,role_id) VALUES (1,1);
INSERT INTO usuarios_roles (usuario_id,role_id) VALUES (2,2);
INSERT INTO usuarios_roles (usuario_id,role_id) VALUES (2,1);



INSERT INTO productos (nombre,precio,create_at) VALUES('Panasonic Pantalla LCD',259990,NOW());
INSERT INTO productos (nombre,precio,create_at) VALUES('Sony Camara digital DSC-W320B',123490,NOW());
INSERT INTO productos (nombre,precio,create_at) VALUES('Apple Ipod suffle',37990,NOW());
INSERT INTO productos (nombre,precio,create_at) VALUES('Sony Notebook Z110',69990,NOW());
INSERT INTO productos (nombre,precio,create_at) VALUES('Bicicleta',69990,NOW());



INSERT INTO facturas (descripcion,observacion,cliente_id,create_at) VALUES('Factura equipos de oficina',null,1,NOW());
INSERT INTO facturas_items (cantidad,factura_id,producto_id) VALUES(1,1,1);
INSERT INTO facturas_items (cantidad,factura_id,producto_id) VALUES(2,1,4);
INSERT INTO facturas_items (cantidad,factura_id,producto_id) VALUES(1,1,5);
INSERT INTO facturas_items (cantidad,factura_id,producto_id) VALUES(1,1,7);

INSERT INTO facturas (descripcion,observacion,cliente_id,create_at) VALUES('Factura Bicicleta','Alguna nota importante',1,NOW());
INSERT INTO facturas_items (cantidad,factura_id,producto_id) VALUES(3,2,5);

