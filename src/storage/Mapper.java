package storage;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Mapper {
    @SuppressWarnings("unchecked")
    @SneakyThrows(IllegalArgumentException.class)
    public static <ENTITY, DTO> List<DTO> mapToDTO(TreeSet<ENTITY> list, Class<DTO> dtoClass, Class<ENTITY> entityClass) {
        List<DTO> dtoList = new ArrayList<>();
        list.forEach(entityElement -> {
            try {
                dtoList.add((DTO) dtoClass.getDeclaredMethod("map", entityClass).invoke(dtoClass.newInstance(), entityElement));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        });
        return dtoList;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows(IllegalArgumentException.class)
    public static <ENTITY, DTO> ENTITY mapFromDTO(DTO dto, Class<DTO> dtoClass, Class<ENTITY> entityClass) {
        ENTITY entity;
        try {
            entity = (ENTITY) entityClass.getDeclaredMethod("map", dtoClass).invoke(entityClass.newInstance(), dto);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
