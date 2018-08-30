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
package sc.iview.commands.demo;
import static sc.iview.commands.MenuWeights.DEMO;
import static sc.iview.commands.MenuWeights.DEMO_SEASHELLS;

import sc.iview.vector.DoubleVector3;

import net.imagej.mesh.Mesh;
import net.imagej.mesh.nio.BufferMesh;

import org.scijava.command.Command;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import sc.iview.SciView;

import cleargl.GLVector;
import graphics.scenery.Material;
import graphics.scenery.Node;

/**
 * Seashell generator, based on the paper by Jorge Picado
 * http://www.mat.uc.pt/~picado/conchas/eng/article.pdf
 *
 * @author Conrad Mearns
 * @author Kyle Harrington
 */
@Plugin(type = Command.class, label = "Seashell Demo", menuRoot = "SciView",
        menu = { @Menu(label = "Demo", weight = DEMO),
                 @Menu(label = "Seashell", weight = DEMO_SEASHELLS) })

public class SeashellDemo implements Command {
    //D, A, alpha, beta, phi, mu, omega, a, b, L, P, W1, W2, N;
    private final String CUSTOM = "Custom";
    private final String TORUS = "Torus";
    private final String BOAT_EAR_MOON = "Boat Ear Moon";
    private final String WENTLETRAP = "Precious Wentletrap";
    private final String TURRITELLA = "Turritella";
    private final String ANCILLA = "Ancilla";
    private final String ARGONAUTA = "Argonauta";

    private double[][] presets = {
      {
        //Torus
        1,
        90,
        Math.toRadians(90),
        Math.toRadians(10),
        0,0,0,
        10,
        10,
        0, 0, 1, 1, 1
      },
      {
        //Boat Ear Moon
        1,
        25,
        Math.toRadians(83),
        Math.toRadians(42),
        Math.toRadians(70),
        Math.toRadians(10),
        Math.toRadians(30),
        12,
        20,
        0, 0, 1, 1, 1
      },
      {
        //Precious Wentletrap
        1,
        90,
        Math.toRadians(86),
        Math.toRadians(10),
        Math.toRadians(-45),
        Math.toRadians(5),
        Math.toRadians(1),
        20,
        20,
        10, Math.toRadians(40), Math.toRadians(180), Math.toRadians(0.4), 8
      },
      {
        //Turritella
        1,
        22.2,
        Math.toRadians(88.9),
        Math.toRadians(4),
        Math.toRadians(55),
        Math.toRadians(1),
        Math.toRadians(-2),
        1.3,
        1.5,
        0, 0, 1, 1, 1
      },
      {
        //Ancilla
        1,
        100,
        Math.toRadians(86),
        Math.toRadians(7),
        0, 0, 0,
        15,
        35,
        0, 0, 1, 1, 1
      },
      {
        //Argonauta
        1,
        2,
        Math.toRadians(80),
        Math.toRadians(90),
        0, 0, 0,
        2,
        1.5,
        0.3,
        Math.toRadians(5),
        Math.toRadians(150),
        Math.toRadians(20),
        30
      }
    };

    @Parameter
    private IOService io;

    @Parameter
    private LogService log;

    @Parameter
    private SciView sciView;

    /**
      * Quantity of rotations the seashell makes
      */
    @Parameter(label = "Turns")
    private double turns;

    /**
      * Resolution of segments per turn along spiral
      */
    @Parameter(label = "Segments per Spiral Turn")
    private int segmentsPerTurn = 64;

    /**
      * Resolution of segments of the generating curve. Effectively makes an
      * ellipse into an N-gon where cseg is N
      */
    @Parameter(label = "Segments per Curve")
    private int cseg = 64;

    /**
      * 14 parameters outlined by the referenced paper.
      */

    private double rotation;

    @Parameter(label = "Choose Preset", choices = {CUSTOM, TORUS, BOAT_EAR_MOON, WENTLETRAP, TURRITELLA, ANCILLA, ARGONAUTA}, persist = false)
    private String preset = CUSTOM;

    @Parameter(label = "Use Radians")
    private boolean inRadians;

    @Parameter
    private double D, A, alpha, beta, phi, mu, omega, a, b, L, P, W1, W2, N;

    public void updateParams() {
      int presetIndex = 0;
      switch(preset){
        default:
          presetIndex = 0;
          break;
        case ARGONAUTA:
          presetIndex++;
        case ANCILLA:
          presetIndex++;
        case TURRITELLA:
          presetIndex++;
        case WENTLETRAP:
          presetIndex++;
        case BOAT_EAR_MOON:
          presetIndex++;
        }

        if(!preset.equals(CUSTOM)){
          D = presets[presetIndex][0];
          A = presets[presetIndex][1];
          alpha = presets[presetIndex][2];
          beta = presets[presetIndex][3];
          phi = presets[presetIndex][4];
          mu = presets[presetIndex][5];
          omega = presets[presetIndex][6];
          a = presets[presetIndex][7];
          b = presets[presetIndex][8];
          L = presets[presetIndex][9];
          P = presets[presetIndex][10];
          W1 = presets[presetIndex][11];
          W2 = presets[presetIndex][12];
          N = presets[presetIndex][13];
        }
    }

    /**
      * Curve function
      * @param s The angle, in radians, that maps to a point on an ellipse
      */
    private double C_ellipse(double s) {
        return Math.pow(Math.pow(Math.cos(s)/a, 2) + Math.pow(Math.sin(s)/b, 2), -0.5);
    }

    /**
      * Node function, allows malformation of the standard ellipse curve to
      * introduce bumps and spines
      * @param s The angle, in radians, that maps to a point on an ellipse
      * @param theta The angle, in radians, that maps to a point on a spiral
      */
    private double C_node(double s, double theta) {
      if(W1 ==0 || W2 == 0 || N == 0) {
          return 0;
      }else {
          double l = ((2 * Math.PI) / N) * ((N * theta)/(2 * Math.PI) - (int)((N * theta)/(2 * Math.PI)));
          return L * Math.exp(-(Math.pow(2*(s-P)/W1, 2) + Math.pow(2*l/W2, 2)));
      }
    }

    /**
      * X, Y, and Z functions for shell generation.
      * @param s The angle, in radians, that maps to a point on an ellipse
      * @param theta The angle, in radians, that maps to a point on a spiral
      * @param curve The combination of all curve-related functions with
      *   corresponding s and theta values
      */
    private double S_x(double theta, double s, double curve) {
        return D * (A * Math.sin(beta) * Math.cos(theta) +
                      Math.cos(s + phi) * Math.cos(theta + omega) * curve -
                      Math.sin(mu) * Math.sin(s + phi) * Math.sin(theta + omega) * curve
                    ) * Math.exp(theta / Math.tan(alpha));
    }

    private double S_y(double theta, double s, double curve) {
        return (A * Math.sin(beta) * Math.sin(theta) +
                  Math.cos(s + phi) * Math.sin(theta + omega) * curve -
                  Math.sin(mu) * Math.sin(s + phi) * Math.cos(theta + omega) * curve
                ) * Math.exp(theta / Math.tan(alpha));
    }

    private double S_z(double theta, double s, double curve) {
        return (-A * Math.cos(beta) +
                  Math.cos(mu) * Math.sin(s + phi) * curve
                ) * Math.exp(theta / Math.tan(alpha));
    }

    @Override
    public void run() {
        if(!inRadians) {
          alpha = Math.toRadians(alpha);
          beta = Math.toRadians(beta);
          phi = Math.toRadians(phi);
          mu = Math.toRadians(mu);
          omega = Math.toRadians(omega);

          P = Math.toRadians(P);
          W1 = Math.toRadians(W1);
          W2 = Math.toRadians(W2);
        }

        updateParams();

        Mesh m = (Mesh)seaShellToMesh(makeShellPoints());
        addMesh(0.0f, 0.0f, 0.0f, m);
    }

    private void addMesh(float x, float y, float z, Mesh m) {
      Node msh = sciView.addMesh(m);
      msh.setPosition(new GLVector(x, y, z));

      msh.fitInto( 15.0f, true );

      Material mat = new Material();
      mat.setAmbient( new GLVector( 1.0f, 0.0f, 0.0f ) );
      mat.setDiffuse( new GLVector( 0.8f, 0.5f, 0.4f ) );
      mat.setSpecular( new GLVector( 1.0f, 1.0f, 1.0f ) );
      mat.setCullingMode(Material.CullingMode.None);

      msh.setMaterial( mat );


      msh.setNeedsUpdate( true );
      msh.setDirty( true );
    }

    public DoubleVector3[][] makeShellPoints() {
      int hseg = (int)(segmentsPerTurn * turns);
      rotation = turns * 2.0f * Math.PI;

      DoubleVector3[][] shell = new DoubleVector3[hseg][cseg];

      for(int i = 0; i < hseg; i++) {
          double theta = i * rotation / hseg;
          for(int j = 0; j < cseg; j++) {
              double s = j * (2 * Math.PI) / cseg;

              double c = C_ellipse(s) + C_node(s, theta);

              shell[i][j] = new DoubleVector3(
                  S_x(theta, s, c),
                  S_y(theta, s, c),
                  S_z(theta, s, c));
          }
      }
      return shell;
    }

    public BufferMesh seaShellToMesh(DoubleVector3[][] shell) {
        int n = shell.length;
        int m = shell[0].length;

        BufferMesh mesh = new BufferMesh(n*m, 2*(n-1)*m);

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                float x = shell[i][j].xf();
                float y = shell[i][j].yf();
                float z = shell[i][j].zf();
                mesh.vertices().add(x, y, z);
            }
        }

        for(int i = 0; i < n-1; i++) {
            for(int j = 0; j < m; j++) {
                mesh.triangles().add(m*i+j, (i+1)*m+(j+1)%m, m*i+(j+1)%m);
                mesh.triangles().add(m*i+j, (i+1)*m+j, (i+1)*m+(j+1)%m);
            }
        }

        return mesh;
    }
}
