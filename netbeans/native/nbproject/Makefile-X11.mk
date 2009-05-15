#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=

# Macros
PLATFORM=GNU-Linux-x86

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/X11/${PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLScreen.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLSurfaceData.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11Blit.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11SurfaceData.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11PlatformScreen.o \
	${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLGraphicsEnvironment.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-X11.mk dist/X11/${PLATFORM}/libcacio-x11.so

dist/X11/${PLATFORM}/libcacio-x11.so: ${OBJECTFILES}
	${MKDIR} -p dist/X11/${PLATFORM}
	${LINK.c} -shared -o dist/X11/${PLATFORM}/libcacio-x11.so -fPIC ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o: ../../src/x11/native/X11GraphicsEnvironment.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o ../../src/x11/native/X11GraphicsEnvironment.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o: ../../src/x11/native/X11VolatileSurfaceManager.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o ../../src/x11/native/X11VolatileSurfaceManager.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLScreen.o: ../../src/sdl/native/SDLScreen.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLScreen.o ../../src/sdl/native/SDLScreen.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLSurfaceData.o: ../../src/sdl/native/SDLSurfaceData.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLSurfaceData.o ../../src/sdl/native/SDLSurfaceData.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11Blit.o: ../../src/x11/native/X11Blit.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11Blit.o ../../src/x11/native/X11Blit.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11SurfaceData.o: ../../src/x11/native/X11SurfaceData.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11SurfaceData.o ../../src/x11/native/X11SurfaceData.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11PlatformScreen.o: ../../src/x11/native/X11PlatformScreen.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/x11/native/X11PlatformScreen.o ../../src/x11/native/X11PlatformScreen.c

${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLGraphicsEnvironment.o: ../../src/sdl/native/SDLGraphicsEnvironment.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native
	${RM} $@.d
	$(COMPILE.c) -g -Werror -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I${openjdk.src}/jdk/src/share/native/common -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/neugens/work_space/netbeans/caciocavallo/netbeans/native/../../src/sdl/native/SDLGraphicsEnvironment.o ../../src/sdl/native/SDLGraphicsEnvironment.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/X11
	${RM} dist/X11/${PLATFORM}/libcacio-x11.so

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
