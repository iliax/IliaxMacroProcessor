package iliaxmacroprocessor.logging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;
/**
 *
 * @author iliax
 */
public class ConsoleAppenderImpl extends ConsoleAppender {

    /** слушатели сообщений логгера */
    private static volatile List<LogEventAppendable> _listeners =
            new CopyOnWriteArrayList<LogEventAppendable>();

    public static boolean APPEND_TO_CONSOLE = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doAppend(LoggingEvent aLogEvent) {
        synchronized(ConsoleAppenderImpl.class){

            if(APPEND_TO_CONSOLE){
                super.doAppend(aLogEvent);
            }
            
            if (!_listeners.isEmpty()) {
                for (LogEventAppendable appendable : _listeners) {
                    appendable.append(aLogEvent);
                }
            }
        }
    }

    /**
     * добавляем слушателя
     *
     * @param aAppendable новый слушатель
     */
    public synchronized  static void addListener(LogEventAppendable aAppendable) {
        _listeners.add(aAppendable);
    }

    /**
     * удаляем из слушателей
     *
     * @param aAppendable для удаления
     */
    public synchronized  static void removeListener(LogEventAppendable aAppendable) {
        _listeners.remove(aAppendable);
    }

    /** интфс слушателей */
    public interface LogEventAppendable {

        /**
         * передает сообщ слушателю
         *
         * @param aLogEvent событие логгера
         */
        public void append(LoggingEvent aLogEvent);

    }

}
