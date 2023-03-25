# GBM Challenge

Proyecto prueba de GBM.

## Requisitos previos

Instalar [Docker Compose](https://docs.docker.com/compose/install/).

## Instalación del proyecto

1. Navega al proyecto `gbm_challenge`.

2. Ejecuta el siguiente comando para iniciar el proyecto utilizando Docker Compose:
  ```
    docker-compose up -d
  ```

## Estructura del proyecto

El proyecto cuenta con 2 servicios `order-service` y `account-service`. El primero esta
encargado de atender las ordenes de compra y venta de acciones y el segundo se ecnarga de la creacion de cuentas
y el manejo de depositos y retiros de dinero en la cuenta.

Notas:
- Los endpoints de cada micro servicio se encuentran en la capa de controladores.
- Las validaciones de reglas de negocio se encuentran en la cpaa de servicios.