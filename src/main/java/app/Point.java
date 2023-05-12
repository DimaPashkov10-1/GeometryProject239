package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import misc.Misc;
import misc.Vector2d;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Класс точки
 */
public class Point {
    /**
     * Координаты точки
     */
    public final Vector2d pos;

    /**
     * Конструктор точки
     *
     * @param pos     положение точки
     */
    @JsonCreator
    public Point(@JsonProperty("pos") Vector2d pos) {
        this.pos = pos;
    }

    /**
     * Получить положение
     * (нужен для json)
     *
     * @return положение
     */
    public Vector2d getPos() {
        return pos;
    }

    /**
     * Строковое представление объекта
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Point{" +
                "pos=" + pos +
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
        Point point = (Point) o;
        return Objects.equals(pos, point.pos);
    }

    /**
     * Получить хэш-код объекта
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
    /**
     * проверка, находится ли точка внутри прямоугольника
     *
     * @return флаг
     */
    public boolean isInRect (Rectangle r) {
        boolean flag = false;
        if (r.point1.x > r.point2.x) {
            double t = r.point1.x;
            r.point1.x = r.point2.x;
            r.point2.x = t;
        }
        if (r.point1.y > r.point2.y) {
            double t = r.point1.y;
            r.point1.y = r.point2.y;
            r.point2.y = t;
        }
        if (this.pos.x >= r.point1.x && this.pos.x <= r.point2.x &&
                this.pos.y >= r.point1.y && this.pos.y <= r.point2.y)
            flag = true;
        return flag;
    }
    /**
     * проверка, находится ли точка в разности
     *
     * @return флаг
     */
    public boolean isComplement (ArrayList<Rectangle> rects) {
        boolean flag = false;
        for (Rectangle r : rects) {
            if (this.isInRect(r) && r.rectSet.equals(Rectangle.RectSet.FIRST_SET))
                flag = true;
            if (this.isInRect(r) && r.rectSet.equals(Rectangle.RectSet.SECOND_SET)) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
