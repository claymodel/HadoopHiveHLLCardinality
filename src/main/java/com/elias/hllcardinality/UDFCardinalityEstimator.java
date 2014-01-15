package com.elias.hllcardinality;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

/*
@Description(name = "",
        value = "_FUNC_(x) -  ",
        extended = "Example:\n"
                + "  > SELECT \n")
*/
// TODO potential set intersection and other utils.
public class UDFCardinalityEstimator extends GenericUDF {

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        return null;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {
        return null;
    }

    @Override
    public String getDisplayString(String[] strings) {
        return null;
    }

}
