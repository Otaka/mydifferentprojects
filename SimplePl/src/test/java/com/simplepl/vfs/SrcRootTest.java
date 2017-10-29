package com.simplepl.vfs;

import com.simplepl.entity.Context;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author sad
 */
public class SrcRootTest {

    private File root;
    private File root2;
    
    private List<TemporaryFolder>tempFolders=new ArrayList<>();

    @Before
    public void setUp() throws IOException {
        root = createTestRootFolder();
        root2 = createTestRootFolder();
        //setup directories;
        File netPackage = new File(root, "com/simplepl/network");
        netPackage.mkdirs();
        createFile(netPackage, "NetUtils.spl", "NetUtils");
        createFile(netPackage, "NetProvider.spl", "NetProvider");

        File netInnerPackage = new File(netPackage, "utils");
        netInnerPackage.mkdirs();
        createFile(netInnerPackage, "NetInnerProvider.spl", "NetInnerProvider");

        File fsPackage = new File(root2, "com/simplepl/fs");
        fsPackage.mkdirs();
        createFile(fsPackage, "FsUtils.spl", "FsUtils");
        createFile(fsPackage, "FsProvider.spl", "FsProvider");

        File orgPackage = new File(root, "org/abc/b");
        orgPackage.mkdirs();
        createFile(orgPackage, "A.spl", "a");
        createFile(orgPackage, "B.spl", "b");

    }

    @After
    public void setDown() {
        for(TemporaryFolder tf:tempFolders){
            tf.delete();
        }
    }

    @Test
    public void testFindObjectByPkgPath() {
        Context context = new Context();
        SrcRoot srcRoot = context.getSrcRoot()
                .addFileSystemRoot(root, context)
                .addFileSystemRoot(root2, context);

        List<AbstractFile> files = srcRoot.getAbstractFiles("com.simplepl.network.NetUtils");
        assertEquals(1, files.size());
        assertEquals("NetUtils", readFile(files.get(0)));

        files = srcRoot.getAbstractFiles("com.simplepl.network.utils.NetInnerProvider");
        assertEquals(1, files.size());
        assertEquals("NetInnerProvider", readFile(files.get(0)));

        files = srcRoot.getAbstractFiles("com.simplepl.network");
        assertEquals(0, files.size());

        files = srcRoot.getAbstractFiles("org.abc.b.A");
        assertEquals(1, files.size());
        assertEquals("a", readFile(files.get(0)));
        files = srcRoot.getAbstractFiles("org.abc.b.B");
        assertEquals(1, files.size());
        assertEquals("b", readFile(files.get(0)));
    }

    public static String readFile(File file) {
        try {
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readFile(AbstractFile file) {
        try(InputStream stream=file.getInputStream()) {
            return IOUtils.toString(stream, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private File createTestRootFolder() throws IOException {
        final TemporaryFolder testFolder = new TemporaryFolder();
        testFolder.create();
        tempFolders.add(testFolder);
        return testFolder.getRoot();
    }

    private static File createFile(File dir, String name, String content) throws FileNotFoundException {
        File file = new File(dir, name);
        try (PrintStream fileOutputStream = new PrintStream(file)) {
            fileOutputStream.print(content);
        }

        return file;
    }
}
