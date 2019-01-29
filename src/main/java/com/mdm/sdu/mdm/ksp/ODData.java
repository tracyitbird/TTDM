package com.mdm.sdu.mdm.ksp;

public class ODData {
    private long o;
    private long d;
//    private double dis;
    public ODData(long o,long d){
        this.o=o;
        this.d=d;
    }
    public ODData(long o,long d,double dis){
        this.o=o;
        this.d=d;
//        this.dis=dis;
    }

    public long getO() {
        return o;
    }
    public long getD() {
        return d;
    }

//    public Double getDis() {
//        return dis;
//    }

    @Override
    public boolean equals(Object od0) {
        // TODO Auto-generated method stub
        ODData p = (ODData) od0;
        return this.o==p.o && this.d==p.d;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        Long code=o+d;
        return code.hashCode();
    }
}
