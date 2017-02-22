package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Hedgefund {

    //this object stores variables that are basically auxiliary in the timestep. only at the end of the timestep are they
    //all used to update the balancesheet. they are set by Behaviour. public/nonpublic has solely been determined by convenience
    //as some need to be accessed in the testcase, this should be improved.

    String name;
    public int id;
    HedgefundBehaviour Behaviour;
    public HedgefundBalanceSheet balanceSheet;

    public int D;
    public int D_;
    public double DEFAULTED;

    public double y;
    public double z;
    public double MC;


    public Hedgefund(String name, int id){
        this.name=name;
        this.D=1;
        this.D_=1;
        this.MC=0;
        this.y=0;
        this.z=0;
        this.Behaviour=new HedgefundBehaviour();
        this.Behaviour.hedgefund=this;
        this.id = id;
        this.balanceSheet = new HedgefundBalanceSheet(this);
    }

    public HedgefundBalanceSheet getBalancesheet(){
        return this.balanceSheet;
    }

    public HedgefundBehaviour getBehaviour(){
        return this.Behaviour;
    }



}