/*
 * #%L
 * Scenery-backed 3D visualization package for ImageJ.
 * %%
 * Copyright (C) 2016 - 2018 SciView developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package sci.iview.commands.snailj;

import static sc.iview.commands.MenuWeights.SNAILJ;
import static sc.iview.commands.MenuWeights.SNAILJ_FRACDIM;
import static sc.iview.commands.snailj.ShellDemo.REMOVETHISGROSSASSMESHTHING;
import sc.iview.SciView;
import sc.iview.process.MeshConverter;

import net.imagej.ops.OpService;
import net.imagej.mesh.nio.BufferMesh;
import net.imagej.mesh.Mesh;

import org.scijava.command.Command;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import graphics.scenery.Material;
import graphics.scenery.Node;

import java.util.List;

import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.ValuePair;

import ij.measure.CurveFitter;

/**
 * Fractal dimension analyzer
 *
 * @author Conrad Mearns
 */
 @Plugin(type = Command.class, label = "SnailJ Fractal Dimension Analyzer", menuRoot = "SciView",
         menu = { @Menu(label = "SnailJ", weight = SNAILJ),
                  @Menu(label = "Analyze Fractal Dimension", weight = SNAILJ_FRACDIM) })

public class FractalDimensionDemo implements Command {

    @Parameter
    private LogService log;

    @Parameter
    private SciView sciView;

    @Parameter
    private OpService op;

    // @Parameter
    // private Mesh mesh;

    @Override
    public void run() {

      Node active = sciView.getActiveNode();

      if(active != null && active instanceof graphics.scenery.Mesh) {
        Mesh m = MeshConverter.toImageJ( (graphics.scenery.Mesh)active );
        double fd = getFractalDimension(m);
        log.info("Fractal Dimension: " + fd);
      } else {
        log.info("Active node is not a mesh");
      }
      // double fd = getFractalDimension((Mesh)REMOVETHISGROSSASSMESHTHING);
      // log.info("Fractal Dimension of new shell: " + fd);

    }

    private double getFractalDimension(Mesh m){
      RandomAccessibleInterval voxelizedMesh = op.geom().voxelization(m);
      List<ValuePair<DoubleType, DoubleType>> toCurveFit = op.topology().boxCount(voxelizedMesh);

      double datax[] = new double[toCurveFit.size()];
      double datay[] = new double[toCurveFit.size()];

      for (int i = 0; i < toCurveFit.size(); i++) {
        datax[i] = toCurveFit.get(i).getA().getRealDouble();
        datay[i] = toCurveFit.get(i).getB().getRealDouble();
      }

      CurveFitter cf = new CurveFitter(datax, datay);
      cf.doFit(CurveFitter.STRAIGHT_LINE);

      log.debug("CurveFitter result: "+cf.getResultString());

      return cf.getParams()[1];
    }
}
