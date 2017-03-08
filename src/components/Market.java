package components;

/**
 * Created by eva on 25/01/2017.
 */
public class Market {
   public double S;
   double Depth;
   double alpha;
   double delta;
   double eta;
   double Old_S;

   //S is the price of the asset, linear impact function. market depth parameter needs to be thoroughly researched, this is
    //a completely random guess.

    public Market(double S, double alpha, double eta, double depth){
        this.S=S;
        this.alpha=alpha;
        this.Depth=depth;
        this.eta=eta;
    }

    public void updateMarket(double q){
        //this doesnt make any sense!
        System.out.println("The market is stepping.");
        System.out.println("The order has been: "+q );
        double S_=S-S*q/Depth;
        System.out.println("New Price is: "+S_);
        delta=(S_-S)/S;
        this.S=S_;

    }

    public void setS(double S_){
        delta=(S_-S)/S;
        this.S=S_;
    }
}
