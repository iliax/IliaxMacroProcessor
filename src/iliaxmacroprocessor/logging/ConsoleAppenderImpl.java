package iliaxmacroprocessor.logging;

import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;
/**
 *
 * @author iliax
 */
public class ConsoleAppenderImpl extends ConsoleAppender {

    /** слушатели сообщений логгера */
    private static List<LogEventAppendable> _listeners =
            new CopyOnWriteArrayList<LogEventAppendable>();

    /** удаленные во время рассылки слушатели (не должны обрабатываться */
    private static List<LogEventAppendable> _listenersToRemove =
            new Vector<LogEventAppendable>();

    /** сообщения на рассылку */
    private Queue<LoggingEvent> _eventsToPost =
            new LinkedBlockingQueue<LoggingEvent>();

    /**
     * посыльщик ивентов(переопределяем фабрику тк для рассылки нужен
     * демон-поток )
     */
    private ExecutorService _eventsPoster = Executors
            .newSingleThreadExecutor(new ThreadFactory() {

                /** {@inheritDoc} */
                @Override
                public Thread newThread(Runnable aR) {
                    Thread thread = new Thread(aR);
                    thread.setDaemon(true);
                    return thread;
                }

            });

    /** задание на рассылку */
    private Runnable _posterRunnable = new Runnable() {

        /** {@inheritDoc} */
        @Override
        public void run() {
            LoggingEvent event = _eventsToPost.poll();
            _listenersToRemove.clear();
            for (LogEventAppendable appendable : _listeners) {
                if (!_listenersToRemove.contains(appendable)) {
                    appendable.append(event);
                }
            }
        }

    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void doAppend(LoggingEvent aLogEvent) {
        super.doAppend(aLogEvent);

        if (!_listeners.isEmpty()) {
            _eventsToPost.add(aLogEvent);
            _eventsPoster.submit(_posterRunnable);
        }
    }

    /**
     * добавляем слушателя
     *
     * @param aAppendable новый слушатель
     */
    public static void addListener(LogEventAppendable aAppendable) {
        _listeners.add(aAppendable);
    }

    /**
     * удаляем из слушателей
     *
     * @param aAppendable для удаления
     */
    public static void removeListener(LogEventAppendable aAppendable) {
        _listeners.remove(aAppendable);
        _listenersToRemove.add(aAppendable);
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
