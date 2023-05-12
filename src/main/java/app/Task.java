package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.*;
import lombok.Getter;
import misc.*;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static app.Colors.*;

/**
 * Класс задачи
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Task {
    /**
     * Текст задачи
     */
    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            На плоскости задано два множества "параллельных"
            прямоугольников. Найти "разность" множеств.
            То есть, все такие точки плоскости, которые лежат
            внутри хотя бы одного прямоугольника первого множества,
            и не лежат ни в одном прямоугольнике второго множества.
            Отобразить найденное множество""";
    /**
     * Счетчики кликов
     */
    private int LMBcount = 0;
    private int RMBcount = 0;
    /**
     * Запоминание первого клика мыши из двух
     */
    private Vector2d LMBtemp = null;
    private Vector2d RMBtemp = null;
    /**
     * коэффициент колёсика мыши
     */
    private static final float WHEEL_SENSITIVE = 0.001f;
    /**
     * Вещественная система координат задачи
     */
    @Getter
    private final CoordinateSystem2d ownCS;
    /**
     * Список прямоугольников
     */
    @Getter
    private final ArrayList<Rectangle> rects;
    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;
    /**
     * Последняя СК окна
     */
    private CoordinateSystem2i lastWindowCS;
    /**
     * Флаг, решена ли задача
     */
    private boolean solved;
    /**
     * Порядок разделителя сетки, т.е. раз в сколько отсечек
     * будет нарисована увеличенная
     */
    private static final int DELIMITER_ORDER = 10;

    /**
     * Задача
     *
     * @param ownCS СК задачи
     * @param rects массив прямоугольников
     */
    @JsonCreator
    public Task(
            @JsonProperty("ownCS") CoordinateSystem2d ownCS,
            @JsonProperty("rects") ArrayList<Rectangle> rects
    ) {
        this.ownCS = ownCS;
        this.rects = rects;
    }

    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;
        // рисуем координатную сетку
        renderGrid(canvas, lastWindowCS);
        // рисуем задачу
        renderTask(canvas, windowCS);
    }

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    private void renderTask(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
            if (solved) {
                for (Rectangle a : rects) {
                    //перебираем все прямоугольники из первого множества и закрашиваем их
                    if (a.rectSet.equals(Rectangle.RectSet.FIRST_SET)){
                        paint.setColor(Misc.getColor(0xFF, 0x00, 0xFF, 0x00));
                        Vector2i po1 = windowCS.getCoords(a.point1.x, a.point1.y, ownCS);
                        Vector2i po2 = windowCS.getCoords(a.point2.x, a.point2.y, ownCS);
                        //для корректного закрашивания устанавливаем point1 как левый верхний угол, а point2 как правый нижний
                        if (po1.x > po2.x) {
                            int t = po1.x;
                            po1.x = po2.x;
                            po2.x = t;
                        }
                        if (po1.y > po2.y) {
                            int t = po1.y;
                            po1.y = po2.y;
                            po2.y = t;
                        }
                        canvas.drawRect(Rect.makeXYWH(po1.x + 1, po1.y + 1, po2.x - po1.x - 1, po2.y - po1.y - 1), paint);
                    }
                }
                //перебираем все прямоугольники из второго множества и закрашиваем их цветом, совпадающим с цветом фона
                //формально - убираем заливку
                for (Rectangle a : rects) {
                    if (a.rectSet.equals(Rectangle.RectSet.SECOND_SET)) {
                        paint.setColor(Misc.getColor(255, 33, 61, 73));
                        Vector2i po1 = windowCS.getCoords(a.point1.x, a.point1.y, ownCS);
                        Vector2i po2 = windowCS.getCoords(a.point2.x, a.point2.y, ownCS);
                        if (po1.x > po2.x) {
                            int t = po1.x;
                            po1.x = po2.x;
                            po2.x = t;
                        }
                        if (po1.y > po2.y) {
                            int t = po1.y;
                            po1.y = po2.y;
                            po2.y = t;
                        }
                        canvas.drawRect(Rect.makeXYWH(po1.x + 1, po1.y + 1, po2.x - po1.x - 1, po2.y - po1.y - 1), paint);
                    }
                }
                /*
                метод рисования нужного множества по точкам, сильно снижает производительность
                for (int i = 0; i < windowCS.getCoords(10, 10, ownCS).x; i++) {
                    for (int j = 0; j < windowCS.getCoords(10, 10, ownCS).y; j++) {
                        Vector2i vi = new Vector2i(i,j);
                        Point p = new Point(ownCS.getCoords(vi.x, vi.y, windowCS));
                        if (p.isComplement(rects)) {
                            canvas.drawRect(Rect.makeXYWH(i,j,1,1),paint);
                            paint.setColor(Misc.getColor(0xFF, 0x00, 0xFF, 0x00));
                        }
                    }
                }*/
            }
            //рисование всех прямоугольников
            for (Rectangle r : rects) {
                paint.setColor(r.getColor());
                Vector2i p1 = windowCS.getCoords(r.point1.x, r.point1.y, ownCS);
                Vector2i p2 = windowCS.getCoords(r.point2.x, r.point2.y, ownCS);
                if (p1.x > p2.x) {
                    int t = p1.x;
                    p1.x = p2.x;
                    p2.x = t;
                }
                if (p1.y > p2.y) {
                    int t = p1.y;
                    p1.y = p2.y;
                    p2.y = t;
                }
                Vector2i p3 = new Vector2i(p1.x, p2.y);
                Vector2i p4 = new Vector2i(p2.x, p1.y);
                canvas.drawLine(p1.x, p1.y, p3.x, p3.y, paint);
                canvas.drawLine(p1.x, p1.y, p4.x, p4.y, paint);
                canvas.drawLine(p2.x, p2.y, p3.x, p3.y, paint);
                canvas.drawLine(p2.x, p2.y, p4.x, p4.y, paint);
            }
        }
        renderGrid(canvas, windowCS);
        canvas.restore();
    }

    /**
     * Рисование сетки
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void renderGrid(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // получаем ширину штриха(т.е. по факту толщину линии)
        float strokeWidth = 0.03f / (float) ownCS.getSimilarity(windowCS).y + 0.5f;
        // создаём перо соответствующей толщины
        try (var paint = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(strokeWidth).setColor(TASK_GRID_COLOR)) {
            // перебираем все целочисленные отсчёты нашей СК по оси X
            for (int i = (int) (ownCS.getMin().x); i <= (int) (ownCS.getMax().x); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(i, 0, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % DELIMITER_ORDER == 0 ? 5 : 2;
                // рисуем вертикальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y + strokeHeight, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y - strokeHeight, paint);
            }
            // перебираем все целочисленные отсчёты нашей СК по оси Y
            for (int i = (int) (ownCS.getMin().y); i <= (int) (ownCS.getMax().y); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(0, i, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % 10 == 0 ? 5 : 2;
                // рисуем горизонтальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x + strokeHeight, windowPos.y, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x - strokeHeight, windowPos.y, paint);
            }
        }
        // восстанавливаем область рисования
        canvas.restore();
    }


    /**
     * Добавить точку
     *
     * @param point1  положение
     * @param point2  положение
     * @param rectSet множество
     */
    public void addRect(Vector2d point1, Vector2d point2, Rectangle.RectSet rectSet) {
        solved = false;
        Rectangle newRect = new Rectangle(point1, point2, rectSet);
        //если прямоугольник очень маленький, его рисование может привести к ошибке
        if ((Math.abs(point1.x - point2.x) < 0.1 || (Math.abs(point1.y - point2.y) < 0.1)))
            PanelLog.info("Задаваемый прямоугольник слишком узкий!");
        else {
            rects.add(newRect);
            PanelLog.info("Прямоугольник " + newRect + " добавлен в " + newRect.getSetName());
        }
    }

    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        // если левая кнопка мыши, добавляем в первое множество
        if (mouseButton.equals(MouseButton.PRIMARY)) {
            LMBcount++;
            if (LMBcount % 2 != 0) {
                LMBtemp = taskPos;
            } else addRect(LMBtemp, taskPos, Rectangle.RectSet.FIRST_SET);
            // если правая, то во второе
        } else if (mouseButton.equals(MouseButton.SECONDARY)) {
            RMBcount++;
            if (RMBcount % 2 != 0) {
                RMBtemp = taskPos;
            } else addRect(RMBtemp, taskPos, Rectangle.RectSet.SECOND_SET);
            // если правая, то во второе
        }
    }

    /**
     * Добавить случайные точки
     *
     * @param cnt кол-во случайных точек
     */
    public void addRandomRects(int cnt) {
        // если создавать точки с полностью случайными координатами,
        // то вероятность того, что они совпадут крайне мала
        // поэтому нужно создать вспомогательную малую целочисленную ОСК
        // для получения случайной точки мы будем запрашивать случайную
        // координату этой решётки (их всего 50х50=2500).
        // после нам останется только перевести координаты на решётке
        // в координаты СК задачи
        CoordinateSystem2i addGrid = new CoordinateSystem2i(50, 50);

        // повторяем заданное количество раз
        for (int i = 0; i < cnt; i++) {
            // получаем случайные координаты на решётке
            Vector2i gridPos = addGrid.getRandomCoords();
            // получаем координаты в СК задачи
            Vector2d p1 = ownCS.getCoords(gridPos, addGrid);
            gridPos = addGrid.getRandomCoords();
            Vector2d p2 = ownCS.getCoords(gridPos, addGrid);
            // сработает примерно в половине случаев
            if (ThreadLocalRandom.current().nextBoolean())
                addRect(p1, p2, Rectangle.RectSet.FIRST_SET);
            else
                addRect(p1, p2, Rectangle.RectSet.SECOND_SET);
        }

    }

    /**
     * Очистить задачу
     */
    public void clear() {
        rects.clear();
        solved = false;
    }

    /**
     * Решить задачу
     */
    public void solve() {
        // задача решена
        // ответ дается в графическом виде, его отображение реализовано в методе paint
        solved = true;
    }

    /**
     * Отмена решения задачи
     */
    public void cancel() {
        solved = false;
    }

    /**
     * проверка, решена ли задача
     *
     * @return флаг
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Масштабирование области просмотра задачи
     *
     * @param delta  прокрутка колеса
     * @param center центр масштабирования
     */
    public void scale(float delta, Vector2i center) {
        if (lastWindowCS == null) return;
        // получаем координаты центра масштабирования в СК задачи
        Vector2d realCenter = ownCS.getCoords(center, lastWindowCS);
        // выполняем масштабирование
        ownCS.scale(1 + delta * WHEEL_SENSITIVE, realCenter);
    }

    /**
     * Получить положение курсора мыши в СК задачи
     *
     * @param x        координата X курсора
     * @param y        координата Y курсора
     * @param windowCS СК окна
     * @return вещественный вектор положения в СК задачи
     */
    @JsonIgnore
    public Vector2d getRealPos(int x, int y, CoordinateSystem2i windowCS) {
        return ownCS.getCoords(x, y, windowCS);
    }

    /**
     * Рисование курсора мыши
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param pos      положение курсора мыши
     */
    public void paintMouse(Canvas canvas, CoordinateSystem2i windowCS, Font font, Vector2i pos) {
        // создаём перо
        try (var paint = new Paint().setColor(TASK_GRID_COLOR)) {
            // сохраняем область рисования
            canvas.save();
            // рисуем перекрестие
            canvas.drawRect(Rect.makeXYWH(0, pos.y - 1, windowCS.getSize().x, 2), paint);
            canvas.drawRect(Rect.makeXYWH(pos.x - 1, 0, 2, windowCS.getSize().y), paint);
            // смещаемся немного для красивого вывода текста
            canvas.translate(pos.x + 3, pos.y - 5);
            // положение курсора в пространстве задачи
            Vector2d realPos = getRealPos(pos.x, pos.y, lastWindowCS);
            // выводим координаты
            canvas.drawString(realPos.toString(), 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }


}