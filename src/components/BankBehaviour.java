package components;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBehaviour {
    Bank bank;
    Market market;
   public double lambda_min = 0.3;
   public double lambda_T=0.4;
   public double kappa_min=0.9;
    public double kappa_T=1.1;
    double beta=0.6;



//these are all actions that are taken in a timestep


    //here the bank checks if its just become insolvent, it updates D in the bank object to reflect whether that has happened this timestep
    public void checkSolvency(){
        if(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()>=this.bank.Balancesheet.getLiability()){
            this.bank.D=1;
        } else {this.bank.D=0;
        System.out.println("I am "+bank.name+" and I have just become insolvent");}
    }

    //these two functions are to find the leverage and discover if its below the minimum. it updates B in the bank object to reflect
    //whether the bank is delevering THIS timestep
    public double returnLeverage(){
        double lambda = (this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()-this.bank.Balancesheet.getLiability())/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getTotalRepo()+this.bank.Balancesheet.getCash());
        return lambda;
    }

    public void checkLeverage(){
        double lambda = (this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()-this.bank.Balancesheet.getLiability())/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getTotalRepo()+this.bank.Balancesheet.getCash());
        if(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getTotalRepo()>0
                && this.bank.D==1 && lambda<lambda_min){
            this.bank.B=1;
            System.out.println("I am "+bank.name+" and I am delevering");
        } else {this.bank.B=0;}
    }

    //now by either Rule1 or Rule2 the bank decides how to delever (and how much), this is stored in variables x,y,z in the bank object
    //reflecting reduction of cash, stocks and repo

    public double amountToDelever(){
        double Gamma=(lambda_T*(this.bank.Balancesheet.getTotalRepo())+this.bank.Balancesheet.getLiability()+(lambda_T-1)*(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*this.bank.B/lambda_T;
        return Gamma;
    }

    public void deleverRule1(){
        double Gamma = amountToDelever();
        this.bank.x=Math.min(Gamma, this.bank.Balancesheet.getTotalRepo());
        if(bank.x>0){System.out.println("I am" +bank.name+" and I am reducing repo funding");}
        if(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()>0){
        this.bank.y=((this.bank.Balancesheet.phi*market.S)/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*(Gamma-this.bank.x);
            this.bank.z=((this.bank.Balancesheet.getCash())/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*(Gamma-this.bank.x);
        }
        if(bank.y>0){System.out.println("I am" +bank.name+" and I am selling assets to delever");}
        else {this.bank.y=0;
        this.bank.z=0;}

    }

    public void deleverRule2(){
        double Gamma = amountToDelever();
        this.bank.x=Math.min(Gamma, this.bank.Balancesheet.getTotalRepo());
        if(bank.x>0){System.out.println("I am " +bank.name+" and I am reducing repo funding");}
        this.bank.y=Math.min(Gamma-this.bank.x, this.bank.Balancesheet.phi*market.S);
        if(bank.y>0){System.out.println("I am " +bank.name+" and I am selling assets to delever");}
        double Gamma_=(lambda_min*(this.bank.Balancesheet.getTotalRepo())+this.bank.Balancesheet.getLiability()+(lambda_min-1)*(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*this.bank.B/lambda_min;
        this.bank.z=Math.max(Math.min(Gamma_-this.bank.x-this.bank.y, this.bank.Balancesheet.getCash()),0);
    }

    //should you be using the LCR, this function checks if delevering hasnt brought you to breach LCR.
    // it updates D_ in the bank object to reflect whether that has happened this timest
    public void checkLCR(){
       if(this.bank.Balancesheet.getCash()-this.bank.z>=kappa_min*this.bank.NO){
           this.bank.D_=1;}
           else{this.bank.D_=0;
           System.out.println("I am "+bank.name+" and I have just defaulted by breaching LCR");}

       }


    //these functions have the purpose of communicating with hedgefunds.
    public double giveFundingUpdate(int i){
        double factor =0;
        if(this.bank.getBalancesheet().getTotalRepo()>0){
                factor=1-(this.bank.x/this.bank.getBalancesheet().getTotalRepo());}
                else{factor=0;}

        if(i==1){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_1*factor;}
        if(i==2){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_2*factor;}
        if(i==3){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_3*factor;}
        if(i==4){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_4*factor;}
        if(i==5){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_5*factor;}
        else{return 0;}

    }

    //when the bank hears from the hedgefund whether it has defaulted, it stores this again in temporary variables in the bank object
    //as Def_i
    public void getDefaultInfo(int i, int Def){
        if(i==1){this.bank.Def_1=Def;}
        else  if(i==2){this.bank.Def_2=Def;}
        else if(i==3){this.bank.Def_3=Def;}
        else if(i==4){this.bank.Def_4=Def;}
        else if(i==5){this.bank.Def_5=Def;}
        else{System.out.println("there is no hedgefund w this number"+i);}

    }


    //these functions use the information stored in the temporary variables in the bank object to place market order and update
    //the actual balancesheet at the end of the timestep.
    public double placeMarketOrder(){
        //NEEDS TO HAPPEN BEFORE UPDATE BALANCESHEET
        double Order=-this.bank.y*this.bank.D*this.bank.D_-this.bank.Balancesheet.phi*market.S*(1-this.bank.D)*(1-this.bank.D_);
        return Order;
    }

    public void updateBalancesheet(){
        double totRepo=giveOldTotalRepo();
        double factor =0;
        if(totRepo>0){
            factor=1-(bank.x/totRepo);}
        else{factor=0;}

        this.bank.Balancesheet.phi=(this.bank.Balancesheet.phi-this.bank.y/market.S)*this.bank.D_*this.bank.D;
        this.bank.Balancesheet.C=(this.bank.Balancesheet.C-this.bank.z)*this.bank.D*this.bank.D_;
        this.bank.Balancesheet.Omega_1=this.bank.Def_1*this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_1*factor;

        this.bank.Balancesheet.Omega_2=this.bank.Def_2*this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_2*factor;

        this.bank.Balancesheet.Omega_3=this.bank.Def_3*this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_3*factor;

        this.bank.Balancesheet.Omega_4=this.bank.Def_4*this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_4*factor;

        this.bank.Balancesheet.Omega_5=this.bank.Def_5*this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_5*factor;
        this.bank.Balancesheet.L=(this.bank.Balancesheet.L-bank.y-bank.z)*bank.D*bank.D_;


    }

    public double giveOldTotalRepo(){return this.bank.getBalancesheet().getTotalRepo();}


    //these are "administrative" methods

    public BankBehaviour(){
        this.bank=null;
    }

    public void printBank(){
        System.out.println(this.bank.name);
    }

    public void setMarket(Market market){
        this.market=market;
    }

    public void setNO(){this.bank.NO=this.beta*this.bank.Balancesheet.getLiability();}

    public double getNO(){return this.bank.NO;}


}
