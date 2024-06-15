package com.pliteratura.proyecto.servicio;

public interface IConvertData {
    <T> T getData(String json, Class<T> tClass);
}
