
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JarErrorFixPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("----------------------------------------------------------------------");
        System.out.println(project.getName());
        System.out.println(project.getBuildDir());
        System.out.println(project.getProjectDir());
        System.out.println(project.getRootProject());
        System.out.println(project.getRootDir());
        System.out.println("----------------------------------------------------------------------");


        if (project.getName().equals("carapp")) {
//            project.getExtensions().getByType(AppExtension.class).registerTransform(new JarErrorFixTransform());

        }



//            project.getTasks().withType(JavaCompile.class, new Action<JavaCompile>() {
//                @Override
//                public void execute(JavaCompile javaCompile) {
//                    System.out.println("JavaCompile task found: " + javaCompile.getName());
//                    javaCompile.doLast(new Action<Task>() {
//                        @Override
//                        public void execute(Task task) {
//                            System.out.println("*******************");
//                            try {
//                                processModifyBytecode();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            });
//        }

    }


//
//    private void processModifyBytecode() throws IOException {
//        System.out.println("开始修复");
//        String projectDir = "D:/AndroidStudioProjects/AndroidTrain/carapp";
//        String jarPath = projectDir + "/libs/asm-error.jar"; // 替换为TT.jar的实际路径
//        Path outputPath = Paths.get(projectDir + "/outputs/asm-error_modified.jar"); // 输出修改后的JAR路径
//
//        try (JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(Paths.get(jarPath)));
//             JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(outputPath))) {
//
//            JarEntry entry;
//            while ((entry = jarInputStream.getNextJarEntry()) != null) {
//                String entryName = entry.getName();
//                if (entryName.equals("com/kstudy/www/ASMWorker.class")) {
//                    byte[] modifiedBytes = modifyClass(jarInputStream.readAllBytes());
//                    JarEntry modifiedEntry = new JarEntry(entryName);
//                    jarOutputStream.putNextEntry(modifiedEntry);
//                    jarOutputStream.write(modifiedBytes);
//                } else {
//                    jarOutputStream.putNextEntry(entry);
//                    jarOutputStream.write(jarInputStream.readAllBytes());
//                }
//                jarOutputStream.closeEntry();
//            }
//        }
//        System.out.println("修复结束");
//    }
//
//    private byte[] modifyClass(byte[] originalBytes) {
//        ClassReader classReader = new ClassReader(originalBytes);
//        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//
//        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, classWriter) {
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//                if ("method1".equals(name) && "()V".equals(desc)) {
//                    // 将方法字节码替换为注释
//                    MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
//                    return new MethodVisitor(Opcodes.ASM7, methodVisitor) {
//                        @Override
//                        public void visitCode() {
//                            super.visitLdcInsn("This method has been commented out.");
//                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "out",
//                                    "()Ljava/io/PrintStream;", false);
//                            super.visitInsn(Opcodes.POP);
//                            super.visitCode();
//                        }
//                    };
//                }
//                return super.visitMethod(access, name, desc, signature, exceptions);
//            }
//        };
//
//        classReader.accept(classVisitor, 0);
//        return classWriter.toByteArray();
//    }

}
