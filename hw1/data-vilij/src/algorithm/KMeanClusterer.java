package algorithm;

import dataprocessors.DataSet;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeanClusterer extends Cluster {


    private List<Point2D> centroids;




    public KMeanClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean toContinue) {
        
        this.dataSet = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.continuous = new AtomicBoolean(toContinue);
        this.numLabels = numberOfClusters;
    }
    public KMeanClusterer()
    {
        this.dataSet = new DataSet();
        this.maxIterations = 0;
        this.updateInterval = 0;
        this.continuous = new AtomicBoolean();
        this.toContinue = new AtomicBoolean();
    }
    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return continuous.get(); }

    @Override
    public void run() {
        toContinue.set(true);
        initializeCentroids();
        int iteration = 1;
        while (iteration++ <= maxIterations && toContinue.get()) {
            assignLabels();
            recomputeCentroids();
            if(iteration % updateInterval == 0)
            {
                flush();
            }
        }
    }
    
    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataSet.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numLabels) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                ++i;
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataSet.getLocations().get(name)).collect(Collectors.toList());

    }

    private void assignLabels() {
        dataSet.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataSet.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {

        IntStream.range(0, numLabels).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataSet.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataSet.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                toContinue.set(false);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}