package components;

import Jama.Matrix;

/**
 * Created by eva on 24/01/2017.
 */
public class Hedgefund {

    //this object stores variables that are basically auxiliary in the timestep. only at the end of the timestep are they
    //all used to update the balancesheet. they are set by Behaviour. public/nonpublic has solely been determined by convenience
    //as some need to be accessed in the testcase, this should be improved.

    String name;
    HedgefundBehaviour Behaviour;
    public int D;
    public int D_;

   public double y;
   public double z;
   public double MC;
    public double alpha;
//    public Matrix repos;
//    public Matrix newFunding;
//    public double[] hedgefundDefaults;
    public int id;
    private HedgefundBalanceSheet balanceSheet;

    public Hedgefund(String name, int id){
        this.name=name;
        this.D=1;
        this.D_=1;
        this.MC=0;
        this.Behaviour=new HedgefundBehaviour();
        this.Behaviour.hedgefund=this;
        this.id = id;
//        this.repos = repos;
//        this.newFunding = newFunding;
//        this.hedgefundDefaults=hedgefundDefaults;
        this.balanceSheet = new HedgefundBalanceSheet(this);
    }


//    public void setBalancesheet(HedgefundBalanceSheet Balancesheet){
//        this.Balancesheet=Balancesheet;
//        Balancesheet.hedgefund=this;
//    }

    public HedgefundBalanceSheet getBalancesheet(){
        return this.balanceSheet;
    }

    public void printBalanceSheet(){
        System.out.println("balanceSheet "+this.name);
        System.out.println("Assets:");
        System.out.println("Stockvalue "+this.balanceSheet.phi*this.getBehaviour().market.S);
        System.out.println("Cash "+this.balanceSheet.C);
        System.out.println("Liabilities:");
        System.out.println("Repo funding "+this.balanceSheet.getTotalFunding());
        System.out.println("---------------------------------------------");


    }

    public void printNewFunding(){
        double[] newFundingColumn = newFunding.transpose().getArray()[id];

        for (double repo : newFundingColumn) {

            System.out.println(repo);
        }

    }

    public void printName(){
        System.out.println(this.name);
    }

    public void printStockValue(){
        System.out.println(this.balanceSheet.phi);
    }

    public HedgefundBehaviour getBehaviour(){
        return this.Behaviour;
    }

    public double getTotalNewFunding(){

        double sum = 0;
        double[] newFundingColumn = newFunding.transpose().getArray()[id];

        for (double repo : newFundingColumn) {
            sum += repo;
        }

        return sum;
    }

}