package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Bank {

    //this object stores variables that are basically auxiliary in the timestep. only at the end of the timestep are they
    //all used to update the balancesheet. they are set by Behaviour. public/nonpublic has solely been determined by convenience
    //as some need to be accessed in the testcase, this should be improved.

    String name;
    public int id;

    public BankBalanceSheet balanceSheet;
    BankBehaviour Behaviour;

    public int D;
    public double D_;
    public double DEFAULTED;
    public int B;
    double x;
    double y;
    double z;
    double y_;
    double z_;
    double NO;



    public Bank(String name, int id){
        this.id = id;
        this.name=name;
        this.D=1;
        this.B=0;
        this.x=0;
        this.y=0;
        this.z=0;
        this.y_=0;
        this.z_=0;
        this.D_=1;
        this.DEFAULTED=0;
        this.Behaviour=new BankBehaviour();
        this.Behaviour.bank=this;
        this.NO=0;
        this.balanceSheet = new BankBalanceSheet(this);

    }

    public void reset(){
        this.D=1;
        this.B=0;
        this.x=0;
        this.y=0;
        this.z=0;
        this.y_=0;
        this.z_=0;
        this.D_=1;
    }


    public BankBalanceSheet getBalanceSheet(){
        return this.balanceSheet;
    }

    public BankBehaviour getBehaviour(){
        return this.Behaviour;
    }




}
