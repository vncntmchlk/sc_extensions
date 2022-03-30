LoadFolder {
	var path, server;
	*new { |path = "/home/vincent/Soundfiles_48khz/", server|
		path = path +/+ "*";
		^super.newCopyArgs(path, server).init;
	}
	init {
		var bufs = (), dirNow = 'root', matchP;
		server = server ? Server.default;
		bufs.load = { |self key|
			server.waitForBoot {
				bufs[key].keysValuesChange{|k v|
					if(v.class == String){Buffer.read(server, v)}{v}
				}
			};
			bufs[key]
		};
		bufs[dirNow] = ();
		matchP = { |p|
			p.pathMatch.sort{|a b| a.isFile}.do{ |newPath|
				if(newPath.isFolder){
					dirNow = PathName(newPath).folderName.asSymbol;
					bufs[dirNow] = ();
					matchP.(newPath ++ "*")
				} {
					if(["wav", "aiff"].includesEqual(newPath.extension)){
						bufs[dirNow][PathName(newPath).fileNameWithoutExtension.asSymbol] = newPath;
					}
				}
			}
		};
		matchP.(path);
		// path.postln;
		^bufs
	}
}
