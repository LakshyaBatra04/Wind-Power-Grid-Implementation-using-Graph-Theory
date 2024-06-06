import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

class PowerLine {
    String cityA;
    String cityB;

    public PowerLine(String cityA, String cityB) {
        this.cityA = cityA;
        this.cityB = cityB;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerLine powerLine = (PowerLine) o;
        return (cityA.equals(powerLine.cityA) && cityB.equals(powerLine.cityB)) ||
               (cityA.equals(powerLine.cityB) && cityB.equals(powerLine.cityA));
    }
}


public class PowerGrid {
    int numCities;
    int numLines;
    String[] cityNames;
    PowerLine[] powerLines;
    HashMap<String,Integer>cityIndex;
    List<List<Integer>>adj;
    static List<PowerLine>bridges;


    public PowerGrid(String filename)  {
        try{
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            numCities = Integer.parseInt(br.readLine());
            numLines = Integer.parseInt(br.readLine());
            cityNames = new String[numCities];
            for (int i = 0; i < numCities; i++) {
                cityNames[i] = br.readLine();
            }
            powerLines = new PowerLine[numLines];
            for (int i = 0; i < numLines; i++) {
                String[] line = br.readLine().split(" ");
                powerLines[i] = new PowerLine(line[0], line[1]);
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }
        
        cityIndex=new HashMap<>();
        adj=new ArrayList<>();
        bridges=new ArrayList<>();

        for(int i=0;i<cityNames.length;i++){
            cityIndex.put(cityNames[i],i);
        }
        for(int i=0;i<numCities;i++){
            adj.add(new ArrayList<>());
        }
        makeAdjList(adj, cityNames, powerLines,cityIndex);
        
    }

    
    private static void makeAdjList(List<List<Integer>>adj,String[] cityNames,PowerLine[] powerLines,HashMap<String,Integer>cityIndex){
        for(PowerLine edge:powerLines){
            String city1=edge.cityA;
            String city2=edge.cityB;

            int index1=cityIndex.get(city1);
            int index2=cityIndex.get(city2);

            adj.get(index1).add(index2);
            adj.get(index2).add(index1);
        }


    }
    private static void tarjanAlgo(int node,int parent,List<List<Integer>>adj,List<List<Integer>>temp,int[] visited,int[] t_in,
                    int[] low,int timer){

        visited[node]=1;
        t_in[node]=timer;
        low[node]=timer;
        timer++;

        for(int nbr:adj.get(node)){
            if(nbr==parent){continue;}
            if(visited[nbr]==-1){
                tarjanAlgo(nbr, node, adj, temp, visited, t_in, low, timer);
                low[node]=Math.min(low[node],low[nbr]);
                if(low[nbr]>t_in[node]){
                    List<Integer>bridge=new ArrayList<>();
                    bridge.add(node);
                    bridge.add(nbr);
                    temp.add(bridge);
                }
            }
            else{
                low[node]=Math.min(low[node],low[nbr]);
            }
        }

    }
    public ArrayList<PowerLine> criticalLines() {
        /*
         * An efficient algorithm to compute the critical transmission lines
         * in the power grid. -->These are basically brides in the graph, so we can use Tarjan's algorithm
         
         * running time: O(m + n), where n is the number of cities and m is the
         * number of transmission lines.
         */

        int n=numCities;
        int[] visited=new int[n];
        int timer=1;
        int[] t_in=new int[n];
        int[] low=new int[n];

        for(int i=0;i<n;i++){
            visited[i]=-1;
            t_in[i]=0;
            low[i]=0;
        }


        List<List<Integer>>temp=new ArrayList<>();

        tarjanAlgo(0,-1,adj,temp,visited,t_in,low,timer);

        ArrayList<PowerLine>ans=new ArrayList<>();
        for(List<Integer>bridge:temp){
            int i=bridge.get(0);
            int j=bridge.get(1);
            ans.add(new PowerLine(cityNames[i],cityNames[j]));
        }
        return ans;
        
    }



    public void preprocessImportantLines() {
     
        List<List<Integer>>temp=new ArrayList<>();
        int[] visited=new int[numCities];
        int[] t_in=new int[numCities];
        int[] low=new int[numCities];
        int timer=1;
        for(int i=0;i<numCities;i++){
            visited[i]=-1;
            t_in[i]=0;
            low[i]=0;
        }
        tarjanAlgo(0, -1, adj, temp, visited, t_in, low, timer);
        for(List<Integer>bridge:temp){
            int i=bridge.get(0);
            int j=bridge.get(1);
            bridges.add(new PowerLine(cityNames[i],cityNames[j]));
        }
        return;
    }

    public static boolean isBridge(PowerLine p){

        for(PowerLine test:bridges){
            if(test.cityA.equals(p.cityA)&&test.cityB.equals(p.cityB)||test.cityB.equals(p.cityA)&&test.cityA.equals(p.cityB)){
                return true;
            }
           
        }
        return false;
    }
    public static void dfs(int src,int dest,List<List<PowerLine>>allPaths,List<PowerLine>currentPath,int[] visited,List<List<Integer>>adj,String[] cityNames){
        if(src==dest){
            allPaths.add(new ArrayList<>(currentPath));
            return;
            }
        
        visited[src]=1;

        for(int nbr:adj.get(src)){
            
            if(visited[nbr]==-1){
                currentPath.add(new PowerLine(cityNames[src],cityNames[nbr]));
                dfs(nbr,dest,allPaths,currentPath,visited,adj,cityNames);
                currentPath.remove(currentPath.size()-1);

            }
        }
        visited[src]=-1;
    }
    public int numImportantLines(String cityA, String cityB) {
       
        int src=cityIndex.get(cityA);
        int dest=cityIndex.get(cityB);


        int[] visited=new int[numCities];
        Arrays.fill(visited,-1);
        
        int ans=0;
        List<List<PowerLine>>allPaths=new ArrayList<>();
        List<PowerLine>currentPath=new LinkedList<>();
        dfs(src,dest,allPaths,currentPath,visited,adj,cityNames);
        List<PowerLine>p=allPaths.get(0);
        for(PowerLine x:p ){
            if(isBridge(x)){
                ans++;
            }
        }
        return ans;
    }
    public static void main(String[] args) {
        PowerGrid pg2 = new PowerGrid("input2.txt");
        ArrayList<PowerLine>p=pg2.criticalLines();
        pg2.preprocessImportantLines();
        System.out.println(pg2.numImportantLines("D", "E"));
        System.out.println(pg2.numImportantLines("K", "N"));

        System.out.println(pg2.numImportantLines("O", "H"));
        System.out.println(pg2.numImportantLines("G", "J"));
        for(PowerLine e:p){
            System.out.println(e.cityA + " "+  e.cityB);
        }
    }
}