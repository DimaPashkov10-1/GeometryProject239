package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import misc.Misc;
import misc.Vector2d;

import java.util.Objects;

/**
 * Класс прямоугольника
 */
public class Rectangle {
    /**
     * Множества
     */
    public enum RectSet {
        /**
         * Первое
         */
        FIRST_SET,
        /**
         * Второе
         */
        SECOND_SET
    }

    /**
     * Множество, которому принадлежит прямоугольник
     */
    protected final RectSet rectSet;
    /**
     * Координаты противоположных углов прямоугольника
     */
    public final Vector2d point1;
    public final Vector2d point2;
    /**
     * Конструктор точки
     *
     * @param point1     положение точки
     * @param point2     положение точки
     * @param setType множество, которому принадлежит прямоугольник
     */
    @JsonCreator
    public Rectangle(@JsonProperty("point1") Vector2d point1, @JsonProperty("point2") Vector2d point2, @JsonProperty("setType") RectSet setType) {
        this.point1 = point1;
        this.point2 = point2;
        this.rectSet = setType;
    }
    /**
     * Получить цвет прямоугольника по её множеству
     *
     * @return цвет прямоугольника
     */
    @JsonIgnore
    public int getColor() {
        return switch (rectSet) {
            case FIRST_SET -> Misc.getColor(0xCC, 0xFF, 0xFF, 0x00);
            case SECOND_SET -> Misc.getColor(0xCC, 0x00, 0xFF, 0xFF);
        };
    }

    /**
     * Получить положение
     * (нужен для json)
     *
     * @return положение
     */
    public Vector2d getPoint1() {
        return point1;
    }
    public Vector2d getPoint2() {
        return point2;
    }
    /**
     * Получить множество
     *
     * @return множество
     */
    public RectSet getSetType() {
        return rectSet;
    }


    /**
     * Получить название множества
     *
     * @return название множества
     */
    @JsonIgnore
    public String getSetName() {
        return switch (rectSet) {
            case FIRST_SET -> "Первое множество";
            case SECOND_SET -> "Второе множество";
        };
    }

    /**
     * Строковое представление объекта
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Rectangle{" +
                "rectSetType=" + rectSet +
                ", point1=" + point1 +
                ", point2=" + point2 +
                '}';
    }

    /**
     * Проверка двух объектов на равенство
     *
     * @param o объект, с которым сравниваем текущий
     * @return флаг, равны ли два объекта
     */
    @Override
    public boolean equals(Object o) {
        // если объект сравнивается сам с собой, тогда объекты равны
        if (this == o) return true;
        // если в аргументе передан null или классы не совпадают, тогда объекты не равны
        if (o == null || getClass() != o.getClass()) return false;
        // приводим переданный в параметрах объект к текущему классу
        Rectangle rectangle = (Rectangle) o;
        return rectSet.equals(rectangle.rectSet) && ((Objects.equals(point1, rectangle.point1) && Objects.equals(point2, rectangle.point2)) ||
                (Objects.equals(point1, rectangle.point2) && Objects.equals(point2, rectangle.point1)));
    }

    /**
     * Получить хэш-код объекта
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(rectSet, point1, point2);
    }
}
