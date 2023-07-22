package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class SamImageGrid extends JPanel {
    private ArrayList<SamImage> samImageList;
    private int MAXHEIGHT = 300;

    public SamImageGrid() {
        samImageList = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    }

    public void addSamImage(SamImage samImage) {
        samImageList.add(samImage);
        updateJpanel();
    }

    public void removeAllSamImage() {
        samImageList.clear();
        updateJpanel();
    }

    public void removeSamImage(int index) {
        if (index >= 0 && index < samImageList.size()) {
            samImageList.remove(index);
            updateJpanel();
        }
    }

    private void updateJpanel() {
        removeAll();
        for (SamImage samImage : samImageList) {

            JPanel panelSamImage = new JPanel(new BorderLayout());
            panelSamImage.setBorder(new EmptyBorder(5, 5, 5, 5));

            JLabel lblImagen = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int width = getWidth();
                    int height = getHeight();

                    if (height > MAXHEIGHT) {
                        height = MAXHEIGHT;
                    }

                    double ratio = (double) samImage.getLayerImage().getWidth() / samImage.getLayerImage().getHeight();
                    width = (int) (height * ratio);

                    int x = (getWidth() - width) / 2;
                    int y = (getHeight() - height) / 2;

                    g.drawImage(samImage.getLayerImage(), x, y, width, height, null);
                }
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(200, MAXHEIGHT);
                }

            };
            panelSamImage.add(lblImagen, BorderLayout.CENTER);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));

            JButton btnEditar = new JButton("Editar");
//            btnEditar.setActionCommand(Integer.toString(i));
//            btnEditar.addActionListener(editarActionListener);
            panelBotones.add(btnEditar);

            JButton btnEliminar = new JButton("Eliminar");
//            btnEliminar.setActionCommand(Integer.toString(i));
//            btnEliminar.addActionListener(eliminarActionListener);
            panelBotones.add(btnEliminar);

            panelSamImage.add(panelBotones, BorderLayout.SOUTH);

            add(panelSamImage);
        }
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int height = super.getPreferredSize().height;
        int preferredHeight = Math.min(height, samImageList.size() * (MAXHEIGHT + 30));
        return new Dimension(super.getPreferredSize().width, preferredHeight);
    }
}