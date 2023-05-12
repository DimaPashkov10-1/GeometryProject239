import app.Point;
import app.Rectangle;
import app.Task;
import misc.CoordinateSystem2d;
import misc.Vector2d;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Класс тестирования
 */
public class UnitTest {
    /**
     * Первый тест
     */
    @Test
    public void test1() { //проверка метода isCompliment
        ArrayList<Rectangle> rects = new ArrayList<>();
        //создаем прямоугольник из первого множества
        Rectangle r1 = new Rectangle(new Vector2d(-1,-2), new Vector2d(3,4), Rectangle.RectSet.FIRST_SET);
        rects.add(r1);
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из его границ
            double randomX = -1.0 + (3.0 - (-1.0)) * r.nextDouble();
            double randomY = -2.0 + (4.0 - (-2.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности
            assert p.isComplement(rects);
        }
    }
    /**
     * Второй тест
     */
    @Test
    public void test2() {
        ArrayList<Rectangle> rects = new ArrayList<>();
        //создаем прямоугольники из первого множества близко к центру
        Rectangle r1 = new Rectangle(new Vector2d(-1,-2), new Vector2d(3,4), Rectangle.RectSet.FIRST_SET);
        rects.add(r1);
        Rectangle r2 = new Rectangle(new Vector2d(2,-1), new Vector2d(-4,5), Rectangle.RectSet.FIRST_SET);
        rects.add(r2);
        Rectangle r3 = new Rectangle(new Vector2d(0,2), new Vector2d(-3,3), Rectangle.RectSet.FIRST_SET);
        rects.add(r3);
        Rectangle r4 = new Rectangle(new Vector2d(-3,-4), new Vector2d(3,4), Rectangle.RectSet.FIRST_SET);
        rects.add(r4);
        //создаем прямоугольник из второго множества, который перекроет все
        Rectangle r5 = new Rectangle(new Vector2d(-8,-9), new Vector2d(7,7), Rectangle.RectSet.SECOND_SET);
        rects.add(r5);
        for (int i = 0; i < 1000; i++) {
            Random r = new Random();
            //перебираем 1000 случайных точек из границ окна
            double randomX = -10.0 + (10.0 - (-10.0)) * r.nextDouble();
            double randomY = -10.0 + (10.0 - (-10.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности - не должно быть ни одной
            assert !p.isComplement(rects);
        }
    }
    /**
     * Третий тест
     */
    @Test
    public void test3() {
        ArrayList<Rectangle> rects = new ArrayList<>();
        //создаем прямоугольники из первого множества
        Rectangle r1 = new Rectangle(new Vector2d(-1,-2), new Vector2d(3,4), Rectangle.RectSet.FIRST_SET);
        rects.add(r1);
        //создаем прямоугольник из второго множества
        Rectangle r2 = new Rectangle(new Vector2d(0,0), new Vector2d(4,5), Rectangle.RectSet.SECOND_SET);
        rects.add(r2);
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из границ второго прямоугольника
            double randomX = 0.0 + (4.0 - (0.0)) * r.nextDouble();
            double randomY = 0.0 + (5.0 - (0.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности - не должно быть ни одной
            assert !p.isComplement(rects);
        }
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из разности (первая часть)
            double randomX = -1.0 + (3.0 - (-1.0)) * r.nextDouble();
            double randomY = -2.0 + (0.0 - (-2.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности
            assert p.isComplement(rects);
        }
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из разности (вторая часть)
            double randomX = -1.0 + (0.0 - (-1.0)) * r.nextDouble();
            double randomY = -2.0 + (4.0 - (-2.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности
            assert p.isComplement(rects);
        }
    }
    /**
     * Четвертый тест
     */
    @Test
    public void test4() {
        ArrayList<Rectangle> rects = new ArrayList<>();
        //создаем прямоугольник из первого множества
        Rectangle r1 = new Rectangle(new Vector2d(3,0), new Vector2d(8,8), Rectangle.RectSet.FIRST_SET);
        rects.add(r1);
        //создаем прямоугольник из второго множества, который "разделяет" первый на две части
        Rectangle r2 = new Rectangle(new Vector2d(2,2), new Vector2d(9,3), Rectangle.RectSet.SECOND_SET);
        rects.add(r2);
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из верхней части прямоугольника
            double randomX = -3.0 + (8.0 - (3.0)) * r.nextDouble();
            double randomY = 0.0 + (8.0 - (0.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности
            assert !p.isComplement(rects);
        }
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из пересечения
            double randomX = -3.0 + (8.0 - (3.0)) * r.nextDouble();
            double randomY = 2.0 + (3.0 - (2.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности - не должно быть ни одной
            assert !p.isComplement(rects);
        }
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            //перебираем 100 случайных точек из нижней части прямоугольника
            double randomX = -3.0 + (8.0 - (3.0)) * r.nextDouble();
            double randomY = 3.0 + (8.0 - (3.0)) * r.nextDouble();
            Point p = new Point(new Vector2d(randomX, randomY));
            //проверяем находятся ли они в разности
            assert !p.isComplement(rects);
        }
    }
}
