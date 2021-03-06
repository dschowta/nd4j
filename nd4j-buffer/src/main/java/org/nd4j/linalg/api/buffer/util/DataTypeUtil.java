package org.nd4j.linalg.api.buffer.util;

import org.nd4j.context.Nd4jContext;
import org.nd4j.linalg.api.buffer.DataBuffer;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manipulates the data type
 * for the nd4j context
 * @author Adam Gibson
 */
public class DataTypeUtil {

    private volatile transient static DataBuffer.Type dtype;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Get the allocation mode from the context
     * @return
     */
    public static  DataBuffer.Type getDtypeFromContext(String dType) {
        switch(dType) {
            case "double": return DataBuffer.Type.DOUBLE;
            case "float": return DataBuffer.Type.FLOAT;
            case "int": return DataBuffer.Type.INT;
            case "half": return DataBuffer.Type.HALF;
            default: return DataBuffer.Type.FLOAT;
        }
    }

    /**
     * Gets the name of the alocation mode
     * @param allocationMode
     * @return
     */
    public static String getDTypeForName(DataBuffer.Type allocationMode) {
        switch(allocationMode) {
            case DOUBLE: return "double";
            case FLOAT: return "float";
            case INT: return "int";
            case HALF: return "half";
            default: return "float";
        }
    }

    /**
     * get the allocation mode from the context
     * @return
     */
    public static DataBuffer.Type getDtypeFromContext() {
        try {
            lock.readLock().lock();

            if (dtype == null) {
                lock.readLock().unlock();
                lock.writeLock().lock();

                if (dtype == null)
                    dtype = getDtypeFromContext(Nd4jContext.getInstance().getConf().getProperty("dtype"));

                lock.writeLock().unlock();
                lock.readLock().lock();
            }

            return dtype;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Set the allocation mode for the nd4j context
     * The value must be one of: heap, java cpp, or direct
     * or an @link{IllegalArgumentException} is thrown
     * @param allocationModeForContext
     */
    public static void setDTypeForContext(DataBuffer.Type allocationModeForContext) {
        try {
            lock.writeLock().lock();

            dtype = allocationModeForContext;

            setDTypeForContext(getDTypeForName(allocationModeForContext));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set the allocation mode for the nd4j context
     * The value must be one of: heap, java cpp, or direct
     * or an @link{IllegalArgumentException} is thrown
     * @param allocationModeForContext
     */
    public static void setDTypeForContext(String allocationModeForContext) {
        if(!allocationModeForContext.equals("double") && !allocationModeForContext.equals("float") && !allocationModeForContext.equals("int") && !allocationModeForContext.equals("half"))
            throw new IllegalArgumentException("Allocation mode must be one of: double,float, or int");
        Nd4jContext.getInstance().getConf().put("dtype",allocationModeForContext);
    }


}
