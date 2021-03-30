# External look and feel changer 

This plugin allows changing IDEA look and feel externally from a script or any other application on the local machine.
To apply change write `dark`/`light` or full LaF name into `tcp://localhost:16666`.

Example usage:
```shell
$ echo dark | nc localhost 16666
$ echo "High contrast" | nc localhost 16666
```

LAFs corresponding to the "dark" and "light" modes as well as the interface and port to listen on can be set from
the configuration panel in the settings under 
`File | Settings | Appearance &amp; Behavior | External LAF Switcher`