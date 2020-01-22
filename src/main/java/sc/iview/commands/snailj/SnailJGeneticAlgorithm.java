package sc.iview.commands.snailj;

import cleargl.GLVector;
import graphics.scenery.BoundingGrid;
import graphics.scenery.volumes.Volume;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.scijava.command.Command;
import org.scijava.command.InteractiveCommand;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.Button;
import org.scijava.widget.NumberWidget;
import sc.iview.SciView;
import sc.iview.event.NodeRemovedEvent;

import static sc.iview.commands.MenuWeights.SNAILJ;
import static sc.iview.commands.MenuWeights.SNAILJ_GA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.HashMap;

import org.scijava.command.CommandService;

/**
 * Genetic Algorithm 
 * 
 * @author Conrad Mearns
 */
@Plugin(type = Command.class, label = "SnailJ Shell Generator", menuRoot = "SciView",
        menu = { @Menu(label = "SnailJ", weight = SNAILJ),
                 @Menu(label = "Start Genetic Algorithm", weight = SNAILJ_GA) })

public class SnailJGeneticAlgorithm extends InteractiveCommand {

    final static Logger logger = LoggerFactory.getLogger(SnailJGeneticAlgorithm.class);

    @Parameter
    private SciView sciView;

    @Parameter(label = "Play speed", min = "1", max="100", style = NumberWidget.SCROLL_BAR_STYLE, persist = false)
    private int playSpeed = 10;

    @Parameter(callback = "iterate")
    private Button iterate;

    @Parameter(callback = "randomize")
    private Button randomize;

    @Parameter(callback = "play")
    private Button play;

    @Parameter(callback = "pause")
    private Button pause;

    // Population
    private Shell shellPop;

    public void play() {
        sciView.animate( playSpeed, this::iterate);
    }

    public void pause() {
        sciView.stopAnimation();
    }

    public void randomize() {

    }

    public void iterate() {
        // Create
        // Randomize
        // Find fitness
        // Delete

        shellPop = newRandomShell();

        CommandService commandService = sciView.getScijavaContext().service(CommandService.class);
        HashMap<String, Object> argmap = new HashMap<>();
        argmap.put("turns", 10);
        argmap.put("segmentsPerTurn", 64);
        argmap.put("cseg", 64);
        argmap.put("bumpiness", 0);
        argmap.put("preset", "Custom");
        argmap.put("inRadians", true);

        argmap.put("D", shellPop.D);
        argmap.put("A", shellPop.A);
        argmap.put("alpha", shellPop.alpha);
        argmap.put("beta", shellPop.beta);
        argmap.put("phi", shellPop.phi);
        argmap.put("mu", shellPop.mu);
        argmap.put("omega", shellPop.omega);
        argmap.put("a", shellPop.a);
        argmap.put("b", shellPop.b);
        argmap.put("L", shellPop.L);
        argmap.put("P", shellPop.P);
        argmap.put("W1", shellPop.W1);
        argmap.put("W2", shellPop.W2);
        argmap.put("N", shellPop.N);


        commandService.run(ShellDemo.class, false, argmap);
    }


    /*
        
    [ERROR] Module threw exception
    java.lang.NullPointerException
        at sc.iview.commands.snailj.ShellDemo.addMesh(ShellDemo.java:348)
        at sc.iview.commands.snailj.ShellDemo.run(ShellDemo.java:319)
        at org.scijava.command.CommandModule.run(CommandModule.java:199)
        at org.scijava.module.ModuleRunner.run(ModuleRunner.java:168)
        at org.scijava.module.ModuleRunner.call(ModuleRunner.java:127)
        at org.scijava.module.ModuleRunner.call(ModuleRunner.java:66)
        at org.scijava.thread.DefaultThreadService.lambda$wrap$2(DefaultThreadService.java:228)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:748)
        
    */

    @Override
    public void run() {
        
        randomize();   
    }

    

    @Override
    public void preview() {
        
    }

    // -- Helper methods --

    final class Shell {
        public double D, A, alpha, beta, phi, mu, omega, a, b, L, P, W1, W2, N;
    }

    // Precious Wentletrap
    // 1,
    // 90,
    // Math.toRadians(86),
    // Math.toRadians(10),
    // Math.toRadians(-45),
    // Math.toRadians(5),
    // Math.toRadians(1),
    // 20,
    // 20,
    // 10,
    // Math.toRadians(40),
    // Math.toRadians(180),
    // Math.toRadians(0.4),
    // 8

    private Shell newRandomShell() {
        Random random = new Random();
        Shell s = new Shell();
        s.D =       1 * random.nextDouble();
        s.A =       90 * random.nextDouble();
        s.alpha =   Math.toRadians(360 * random.nextDouble());
        s.beta =    Math.toRadians(360 * random.nextDouble());
        s.phi =     Math.toRadians(360 * random.nextDouble());
        s.mu =      Math.toRadians(360 * random.nextDouble());
        s.omega =   Math.toRadians(360 * random.nextDouble());
        s.a =       50 * random.nextDouble();
        s.b =       50 * random.nextDouble();
        s.L =       50 * random.nextDouble();
        s.P =       Math.toRadians(360 * random.nextDouble());
        s.W1 =      Math.toRadians(360 * random.nextDouble());
        s.W2 =      Math.toRadians(360 * random.nextDouble());
        s.N =       24 * random.nextDouble();
        return s;
    }

}
