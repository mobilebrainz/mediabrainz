package app.mediabrainz.util.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;


/*
Данный эвент потребляет событие для первого обсервера. Другие обсерверы могут получить контент только из peekContent(),
что несёт большие проблемы:
 - все обсерверы кроме первого, не могут потреблять событие
 - при множественном асинхронном вызове обсерверов, невозможно определить кто первый, поэтому нельзя использовать этот
 паттерн при множественном асинхронном наблюдении

 Вывод: использовать только при синхронном наблюдении, где важно только первое потребление события.
 Например, произвести действие при событии (действия требуют потребление событие) и затем вывести результаты события в лог или ui
 (графические выводы не требуют потребления, а скорее наоборот, нуждаются в повторении события).

 Вывод:
    - для событий с множеством синхронных подписчиком-наблюдателем, из которых только первый подписчик потребляет событие, а остальные
        только наблюдают,  использовать Event(@NonNull T content)
    - для наблюдения значений LiveData, у которого потребление события изменения значения зависит от значения LiveData, использовать
         Event(@NonNull T content, boolean consumed). Это обычно ибпользуется при CRUD-запросах
 */
public class Event<T> {

    private final T content;
    private boolean hasBeenHandled;
    private boolean consumed;

    public Event(@NonNull T content) {
        this.content = content;
        this.consumed = true;
    }

    public Event(@NonNull T content, boolean consumed) {
        this.content = content;
        this.consumed = consumed;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    @Nullable
    public T getContentIfNotHandled() {
        if(consumed) {
            if (hasBeenHandled) {
                return null;
            } else {
                hasBeenHandled = true;
                return content;
            }
        } else {
            return content;
        }
    }

    @NonNull
    public T peekContent() {
        return content;
    }
}
