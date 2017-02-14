package components;

import Jama.Matrix;

/**
 * Created by eva on 24/01/2017.
 */
public class Bank {

    //this object stores variables that are basically auxiliary in the timestep. only at the end of the timestep are they
    //all used to update the balancesheet. they are set by Behaviour. public/nonpublic has solely been determined by convenience
    //as some need to be accessed in the testcase, this should be improved.

    String name;
   public int D;
   public int B;
     double x;
     double y;
     double z;
    public double D_;
    double NO;

//    public Matrix repos;
//    public Matrix newFunding;
//    public double[] hedgefundDefaults;
    public int id;


    BankBalanceSheet balanceSheet;
    BankBehaviour Behaviour;

    public Bank(String name, int id){
        this.id = id;
        this.name=name;
        this.D=1;
        this.B=0;
        this.x=0;
        this.y=0;
        this.z=0;
        this.D_=1;
        this.Behaviour=new BankBehaviour();
        this.Behaviour.bank=this;
        this.NO=0;
//        this.repos=repos;
//        this.newFunding=newFunding;
//        this.hedgefundDefaults=hedgefundDefaults;
        this.balanceSheet = new BankBalanceSheet(this);

    }

//    public void setBalanceSheet(BankBalanceSheet Balancesheet){
//        this.balanceSheet =Balancesheet;
//        Balancesheet.bank=this;
//    }

    public BankBalanceSheet getBalanceSheet(){
        return this.balanceSheet;
    }

    public void printBalanceSheet(){
        System.out.println("balanceSheet "+this.name);
        System.out.println("Assets:");
        System.out.println("Matchbook Repo funding "+this.balanceSheet.getTotalRepo());
        System.out.println("Stockvalue "+this.balanceSheet.phi*this.getBehaviour().market.S);
        System.out.println("Cash "+this.balanceSheet.C);
        System.out.println("Liabilities:");
        System.out.println("Matchbook Repo funding "+this.balanceSheet.getTotalRepo());
        System.out.println("General Liabilities "+this.balanceSheet.L);
        System.out.println("---------------------------------------------");
    }

    public void printName(){
        System.out.println(this.name);
    }

    public void printStockAmount(){
        System.out.println(this.balanceSheet.phi);
    }

    public BankBehaviour getBehaviour(){
        return this.Behaviour;
    }




}
