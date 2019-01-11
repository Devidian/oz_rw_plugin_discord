/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omegazirkel.tools;

import java.nio.file.Path;

/**
 *
 * @author Maik
 */
public interface FileChangeListener {

    public void onFileChangeEvent(Path file);
}
