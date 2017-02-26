package components;

import java.lang.invoke.LambdaConversionException;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBehaviour {
    Bank bank;
    Market market;
    InfoExchange infoExchange;
   public double lambda_M = 0.03;
   public double lambda_B = 0.055;
   public double lambda_T=0.07;
   public double kappa_min=0.9;
    public double kappa_T=1.1;
    double beta=0.10;



//these are all actions that are taken in a timestep

    //these two functions find the leverage and check if its below the minimum, triggering default
    public double returnLeverage(){
        double lambda = (this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()-this.bank.balanceSheet.getLiability())/(this.bank.balanceSheet.totalAssets());
        return lambda;
    }

    public void checkMinLev(){
        if(returnLeverage()<lambda_M){
            this.bank.D=0;
            System.out.println("I am "+bank.name+" and I am defaulting by dropping below minimum leverage. My leverage is "+returnLeverage());
        } else {this.bank.D=1;}

        this.infoExchange.bankDefaults1[bank.id]=this.bank.D;

    }

    //this function checks whether the bank has to delever

    public void checkBufLev(){

        if(lambda_M<=returnLeverage() && returnLeverage()<lambda_B){
            this.bank.B=1;
            System.out.println("I am "+bank.name+"and I am delevering, as my leverage is "+returnLeverage()+" while my buffer leverage is "+lambda_B);
        } else {this.bank.B=0;}

    }



    //now by either Rule1 or Rule2 the bank decides how to delever (and how much), this is stored in variables x,y,z in the bank object
    //reflecting reduction of cash, stocks and repo

    public double amountToDelever(){
        double Gamma=(lambda_T*(this.bank.balanceSheet.getTotalRepo())+this.bank.balanceSheet.getLiability()+(lambda_T-1)*(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()))*this.bank.B/lambda_T;
        return Gamma;
    }

    public void deleverRule1(){

        double Gamma = amountToDelever();
        this.bank.x=(bank.balanceSheet.getTotalRepo()/bank.balanceSheet.totalAssets())*Gamma;
        this.bank.y=((bank.balanceSheet.phi*market.S)/bank.balanceSheet.totalAssets())*Gamma;
        this.bank.z=(bank.balanceSheet.C/bank.balanceSheet.totalAssets())*Gamma;
    }

    public void deleverRule2(){
        double Gamma = amountToDelever();
        double SpareCashUsed=Math.min(Gamma, spareCash());
        double GammaSpare=Gamma-SpareCashUsed;

//        this.bank.x=Math.min(GammaSpare, this.bank.balanceSheet.getTotalRepo());
//        this.bank.y=Math.min(GammaSpare-this.bank.x, this.bank.balanceSheet.phi*market.S);
        this.bank.x=GammaSpare*bank.getBalanceSheet().getTotalRepo()/bank.getBalanceSheet().totalAssets();
        if(bank.x>0){System.out.println("I am " +bank.name+" and I am reducing repo funding by"+bank.x);}

        this.bank.y=GammaSpare*bank.getBalanceSheet().phi*market.S/bank.getBalanceSheet().totalAssets();
        if(bank.y>0){System.out.println("I am " +bank.name+" and I am selling assets to delever, value"+bank.y);}

        double Gamma_=(lambda_B*(this.bank.balanceSheet.getTotalRepo())+this.bank.balanceSheet.getLiability()+(lambda_B-1)*(this.bank.balanceSheet.phi*market.S+this.bank.balanceSheet.getCash()))*this.bank.B/lambda_B;

        this.bank.z=Math.max(Gamma_-spareCash()-bank.x-bank.y,0)+Math.min(Gamma, spareCash());
        if(bank.z>0){System.out.println("I am " +bank.name+" and I decreasing cash in total by"+bank.z);}
    }

    //these functions have the purpose of communicating with hedgefunds about updated funding.
    public void giveFundingUpdate(){
        int N_HEDGEFUNDS=infoExchange.repos.getColumnDimension();
        double factor =0;
        if(this.bank.getBalanceSheet().getTotalRepo()>0){
                factor=1-(this.bank.x/this.bank.getBalanceSheet().getTotalRepo());}
                else{factor=0;}

         for(int i=0; i<N_HEDGEFUNDS; i++){
            infoExchange.newFunding.set(bank.id, i, factor*infoExchange.repos.get(bank.id,i));
         }
    }

    //now it's time for the hedgefunds to act, until they inform banks of their defaults.

    //this is how much THE BANK has to add to pay back the cashprovider
    public double findTotalPayback(){
        double totalPayBack=0;

        double[] payBackColumn = infoExchange.repayments.getArray()[bank.id];
        double[] reposRow = infoExchange.repos.getArray()[bank.id];

        for (int i=0; i<payBackColumn.length;i++){
            totalPayBack += Math.max((reposRow[i]-payBackColumn[i])*(1-infoExchange.hedgefundDefaults[i]),0);
        }

        if (totalPayBack > 0 && bank.D==1) {


        System.out.println("bank"+bank.id+"has to cough up "+totalPayBack+"for the cashprovider");}

        return totalPayBack;

    }

    public void performPayback() {
        if (findTotalPayback() > 0) {
            if ((bank.getBalanceSheet().phi * market.S + bank.getBalanceSheet().C -findTotalPayback()-bank.getBalanceSheet().L)/(bank.getBalanceSheet().totalAssets()-findTotalPayback())<lambda_M && this.bank.D==1){
                this.bank.D_=0;
                System.out.println("I am " + bank.name + " and I am defaulting as I can't pay back the cashprovider");
            } else{
                this.bank.D_=1;
                payBack2();
            }
            this.infoExchange.bankDefaults2[bank.id] = this.bank.D_;

        }
    }


    public void payBack1(){
        double Lambda = findTotalPayback();
        double CashAssets=bank.balanceSheet.phi*market.S+bank.getBalanceSheet().C;
        this.bank.y_=((bank.balanceSheet.phi*market.S)/CashAssets)*Lambda;
        this.bank.z_=(bank.balanceSheet.C/CashAssets)*Lambda;

    }

    public void payBack2(){
        double SpareCash=bank.balanceSheet.C-bank.z-minCash();
        double LambdaMore = Math.max(findTotalPayback()-SpareCash,0);
        bank.y_=Math.min(LambdaMore,bank.balanceSheet.phi*market.S);
        bank.z_=Math.max(LambdaMore-bank.y_-SpareCash,0)+Math.min(findTotalPayback(), SpareCash);

    }


    //these functions use the information stored in the temporary variables in the bank object to place market order and update
    //the actual balancesheet at the end of the timestep.
    public double placeMarketOrder(){
        //NEEDS TO HAPPEN BEFORE UPDATE BALANCESHEET


        double Order=-(this.bank.y_+this.bank.y)*this.bank.D*this.bank.D_-this.bank.balanceSheet.phi*market.S*(1-this.bank.D)-this.bank.balanceSheet.phi*market.S*(1-this.bank.D_);
        return Order;
    }

    public void updateBalancesheet(){
        int N_HEDGEFUNDS=infoExchange.repos.getColumnDimension();

        this.bank.balanceSheet.phi=(this.bank.balanceSheet.phi-((this.bank.y+this.bank.y_)/market.S))*this.bank.D_*this.bank.D;
        this.bank.balanceSheet.C=(this.bank.balanceSheet.C-this.bank.z-this.bank.z_)*this.bank.D*this.bank.D_;

        int i;
        for(i=0; i<N_HEDGEFUNDS;i++){
            infoExchange.repos.set(bank.id, i, infoExchange.hedgefundDefaults[i]*infoExchange.newFunding.get(bank.id,i)*infoExchange.loanTerminationsHF.get(bank.id,i));
        }
        this.bank.balanceSheet.L=(this.bank.balanceSheet.L-bank.y-bank.z)*bank.D*bank.D_;

        if(this.bank.D==0||this.bank.D_==0){
            this.bank.DEFAULTED=1;
        }

        this.bank.B=0;
        this.bank.D=1;
        this.bank.D_=1;


    }

    //these are "administrative" methods

    public BankBehaviour(){
        this.bank=null;
    }

    public void setMarket(Market market){
        this.market=market;
    }

    public void setInfoExchange(InfoExchange infoExchange){
        this.infoExchange=infoExchange;
    }

    public void setLeveragePreferences(double target, double buffer){this.lambda_T=target; this.lambda_B=buffer;}


    public void setNO(){this.bank.NO=this.beta*this.bank.balanceSheet.getLiability();}

    public double minCash(){return this.bank.NO*kappa_min;}

    public double spareCash(){ return Math.max(bank.balanceSheet.C-minCash(), 0);}

    public double getNO(){return this.bank.NO;}


}
