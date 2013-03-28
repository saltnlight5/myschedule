package myschedule.web;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Zemian Deng
 */
public abstract class AbstractService implements Service {
    AtomicBoolean inited = new AtomicBoolean(false);

    public boolean isInited() {
        return inited.get();
    }

    @Override
    public void init() {
        if (inited.get())
            return;
        initService();
        inited.set(true);
    }

    @Override
    public void destroy() {
        if (!inited.get())
            return;
        destroyService();
        inited.set(false);
    }

    public abstract void initService();
    public abstract void destroyService();
}
